package trackup.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static trackup.logic.parser.CliSyntax.PREFIX_ADDRESS;
import static trackup.logic.parser.CliSyntax.PREFIX_CATEGORY;
import static trackup.logic.parser.CliSyntax.PREFIX_EMAIL;
import static trackup.logic.parser.CliSyntax.PREFIX_EVENT_CONTACT;
import static trackup.logic.parser.CliSyntax.PREFIX_EVENT_END;
import static trackup.logic.parser.CliSyntax.PREFIX_EVENT_START;
import static trackup.logic.parser.CliSyntax.PREFIX_EVENT_TITLE;
import static trackup.logic.parser.CliSyntax.PREFIX_NAME;
import static trackup.logic.parser.CliSyntax.PREFIX_PHONE;
import static trackup.logic.parser.CliSyntax.PREFIX_TAG;
import static trackup.testutil.Assert.assertThrows;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import trackup.commons.core.index.Index;
import trackup.logic.commands.exceptions.CommandException;
import trackup.model.AddressBook;
import trackup.model.Model;
import trackup.model.person.NameContainsKeywordsPredicate;
import trackup.model.person.Person;
import trackup.testutil.EditPersonDescriptorBuilder;

/**
 * Contains helper methods for testing commands.
 */
public class CommandTestUtil {

    public static final String VALID_NAME_AMY = "Amy Bee";
    public static final String VALID_NAME_BOB = "Bob Choo";
    public static final String VALID_PHONE_AMY = "11111111";
    public static final String VALID_PHONE_BOB = "22222222";
    public static final String VALID_EMAIL_AMY = "amy@example.com";
    public static final String VALID_EMAIL_BOB = "bob@example.com";
    public static final String VALID_ADDRESS_AMY = "Block 312, Amy Street 1";
    public static final String VALID_ADDRESS_BOB = "Block 123, Bobby Street 3";
    public static final String VALID_TAG_HUSBAND = "husband";
    public static final String VALID_TAG_FRIEND = "friend";
    public static final String VALID_CATEGORY_CLIENT = "Client";
    public static final String VALID_CATEGORY_INVESTOR = "Investor";

    public static final String NAME_DESC_AMY = " " + PREFIX_NAME + VALID_NAME_AMY;
    public static final String NAME_DESC_BOB = " " + PREFIX_NAME + VALID_NAME_BOB;
    public static final String PHONE_DESC_AMY = " " + PREFIX_PHONE + VALID_PHONE_AMY;
    public static final String PHONE_DESC_BOB = " " + PREFIX_PHONE + VALID_PHONE_BOB;
    public static final String EMAIL_DESC_AMY = " " + PREFIX_EMAIL + VALID_EMAIL_AMY;
    public static final String EMAIL_DESC_BOB = " " + PREFIX_EMAIL + VALID_EMAIL_BOB;
    public static final String ADDRESS_DESC_AMY = " " + PREFIX_ADDRESS + VALID_ADDRESS_AMY;
    public static final String ADDRESS_DESC_BOB = " " + PREFIX_ADDRESS + VALID_ADDRESS_BOB;
    public static final String TAG_DESC_FRIEND = " " + PREFIX_TAG + VALID_TAG_FRIEND;
    public static final String TAG_DESC_HUSBAND = " " + PREFIX_TAG + VALID_TAG_HUSBAND;
    public static final String CATEGORY_DESC_CLIENT = " " + PREFIX_CATEGORY + VALID_CATEGORY_CLIENT;
    public static final String CATEGORY_DESC_INVESTOR = " " + PREFIX_CATEGORY + VALID_CATEGORY_INVESTOR;

    public static final String INVALID_NAME_DESC = " " + PREFIX_NAME + "James&"; // '&' not allowed in names
    public static final String INVALID_PHONE_DESC = " " + PREFIX_PHONE + "911a"; // 'a' not allowed in phones
    public static final String INVALID_EMAIL_DESC = " " + PREFIX_EMAIL + "bob!yahoo"; // missing '@' symbol
    public static final String INVALID_ADDRESS_DESC = " " + PREFIX_ADDRESS; // empty string not allowed for addresses
    public static final String INVALID_TAG_DESC = " " + PREFIX_TAG + "hubby*"; // '*' not allowed in tags
    public static final String INVALID_CATEGORY_DESC = " " + PREFIX_CATEGORY + "no_category";
    // not one of {"Client", "Investor", "Partner", "Other"}

    public static final String VALID_EVENT_TITLE = "Meeting";
    public static final String VALID_EVENT_START = "2025-04-01 14:00";
    public static final String VALID_EVENT_END = "2025-04-01 15:00";
    public static final String VALID_EVENT_INDEX_1 = "1";
    public static final String VALID_EVENT_INDEX_2 = "3";

    public static final String EVENT_TITLE_DESC = " " + PREFIX_EVENT_TITLE + VALID_EVENT_TITLE;
    public static final String EVENT_START_DESC = " " + PREFIX_EVENT_START + VALID_EVENT_START;
    public static final String EVENT_END_DESC = " " + PREFIX_EVENT_END + VALID_EVENT_END;
    public static final String EVENT_CONTACT_DESC_1 = " " + PREFIX_EVENT_CONTACT + VALID_EVENT_INDEX_1;
    public static final String EVENT_CONTACT_DESC_2 = " " + PREFIX_EVENT_CONTACT + VALID_EVENT_INDEX_2;

    public static final String INVALID_EVENT_TITLE_DESC = " " + PREFIX_EVENT_TITLE + "";
    public static final String INVALID_EVENT_START_DESC = " " + PREFIX_EVENT_START + "not-a-date";
    public static final String INVALID_EVENT_END_DESC = " " + PREFIX_EVENT_END + "bad-date";
    public static final String INVALID_EVENT_CONTACT_DESC = " " + PREFIX_EVENT_CONTACT + "a";

    public static final String PREAMBLE_WHITESPACE = "\t  \r  \n";
    public static final String PREAMBLE_NON_EMPTY = "NonEmptyPreamble";

    public static final EditCommand.EditPersonDescriptor DESC_AMY;
    public static final EditCommand.EditPersonDescriptor DESC_BOB;

    static {
        DESC_AMY = new EditPersonDescriptorBuilder().withName(VALID_NAME_AMY)
                .withPhone(VALID_PHONE_AMY).withEmail(VALID_EMAIL_AMY).withAddress(VALID_ADDRESS_AMY)
                .withTags(VALID_TAG_FRIEND).withCategory(VALID_CATEGORY_CLIENT).build();
        DESC_BOB = new EditPersonDescriptorBuilder().withName(VALID_NAME_BOB)
                .withPhone(VALID_PHONE_BOB).withEmail(VALID_EMAIL_BOB).withAddress(VALID_ADDRESS_BOB)
                .withTags(VALID_TAG_HUSBAND, VALID_TAG_FRIEND).withCategory(VALID_CATEGORY_INVESTOR).build();
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - the returned {@link CommandResult} matches {@code expectedCommandResult} <br>
     * - the {@code actualModel} matches {@code expectedModel}
     */
    public static void assertCommandSuccess(Command command, Model actualModel, CommandResult expectedCommandResult,
                                            Model expectedModel) {
        try {
            CommandResult result = command.execute(actualModel);
            assertEquals(expectedCommandResult, result);
            assertEquals(expectedModel, actualModel);
        } catch (CommandException ce) {
            throw new AssertionError("Execution of command should not fail.", ce);
        }
    }

    /**
     * Convenience wrapper to {@link #assertCommandSuccess(Command, Model, CommandResult, Model)}
     * that takes a string {@code expectedMessage}.
     */
    public static void assertCommandSuccess(Command command, Model actualModel, String expectedMessage,
                                            Model expectedModel) {
        CommandResult expectedCommandResult = new CommandResult(expectedMessage);
        assertCommandSuccess(command, actualModel, expectedCommandResult, expectedModel);
    }

    /**
     * Executes the given {@code command}, confirms that <br>
     * - a {@code CommandException} is thrown <br>
     * - the CommandException message matches {@code expectedMessage} <br>
     * - the address book, filtered person list and selected person in {@code actualModel} remain unchanged
     */
    public static void assertCommandFailure(Command command, Model actualModel, String expectedMessage) {
        // we are unable to defensively copy the model for comparison later, so we can
        // only do so by copying its components.
        AddressBook expectedAddressBook = new AddressBook(actualModel.getAddressBook());
        List<Person> expectedFilteredList = new ArrayList<>(actualModel.getFilteredPersonList());

        assertThrows(CommandException.class, expectedMessage, () -> command.execute(actualModel));
        assertEquals(expectedAddressBook, actualModel.getAddressBook());
        assertEquals(expectedFilteredList, actualModel.getFilteredPersonList());
    }
    /**
     * Updates {@code model}'s filtered list to show only the person at the given {@code targetIndex} in the
     * {@code model}'s address book.
     */
    public static void showPersonAtIndex(Model model, Index targetIndex) {
        assertTrue(targetIndex.getZeroBased() < model.getFilteredPersonList().size());

        Person person = model.getFilteredPersonList().get(targetIndex.getZeroBased());
        final String[] splitName = person.getName().fullName.split("\\s+");
        model.updateFilteredPersonList(new NameContainsKeywordsPredicate(Arrays.asList(splitName[0])));

        assertEquals(1, model.getFilteredPersonList().size());
    }
}
