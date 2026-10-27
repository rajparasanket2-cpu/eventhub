/**
 * Adds a book to the live Library API, reads it back by ID, then deletes it.
 * The server is shared, so each run uses a unique ISBN.
 */

/** The Addbook.php request body; the API takes the aisle as a string. */
function buildAddBookRequest(isbn) {
  return {
    name: 'Learn Appium Automation with Java',
    isbn,
    aisle: '227',
    author: 'John foe',
  };
}

describe('Library API', () => {
  /** The ID of the book this test added, so afterEach can delete it when the test fails early. */
  let addedBookId = null;

  afterEach(() => {
    // Safety net: delete the book only when the test failed before it deleted it
    if (addedBookId) {
      cy.request({
        method: 'POST',
        url: '/Library/DeleteBook.php',
        body: { ID: addedBookId },
        failOnStatusCode: false,
      });
      addedBookId = null;
    }
  });

  it('TC82: a book added through Addbook.php is returned with the same details by GetBook.php?ID, then deleted', () => {
    // -- Step 1: build a book with a unique ISBN, since the server rejects duplicate IDs --
    const isbn = `cy${Date.now().toString(36)}`;
    const book = buildAddBookRequest(isbn);

    // -- Step 2: add the book, and keep its ID before asserting so cleanup still runs if an assertion fails --
    cy.request('POST', '/Library/Addbook.php', book).then((addResponse) => {
      addedBookId = addResponse.body.ID;
      cy.log(`Added book ID: ${addedBookId}`);

      // -- Step 3: the book was added, and the server built its ID from isbn + aisle --
      expect(addResponse.status, 'Addbook.php status').to.eq(200);
      expect(addResponse.body.Msg, 'Msg').to.eq('successfully added');
      expect(addResponse.body.ID, 'ID').to.eq(book.isbn + book.aisle);
    });

    // -- Step 4: get the book back by its ID; GetBook.php answers with an array of books --
    cy.then(() =>
      cy.request({ url: '/Library/GetBook.php', qs: { ID: addedBookId } }),
    ).then((getResponse) => {
      expect(getResponse.status, 'GetBook.php status').to.eq(200);

      // -- Step 5: exactly one book matches the ID, with the same details that were added --
      expect(getResponse.body, 'books matching the ID').to.have.length(1);
      const [returned] = getResponse.body;
      expect(returned.book_name, 'book_name').to.eq(book.name);
      expect(returned.isbn, 'isbn').to.eq(book.isbn);
      expect(returned.aisle, 'aisle').to.eq(book.aisle);
      expect(returned.author, 'author').to.eq(book.author);
    });

    // -- Step 6: delete the book that was added --
    cy.then(() =>
      cy.request('POST', '/Library/DeleteBook.php', { ID: addedBookId }),
    ).then((deleteResponse) => {
      expect(deleteResponse.status, 'DeleteBook.php status').to.eq(200);
      expect(deleteResponse.body.msg, 'msg').to.eq('book is successfully deleted');
    });

    // -- Step 7: the deleted book can no longer be found by its ID --
    cy.then(() => {
      const deletedBookId = addedBookId;
      addedBookId = null;
      return cy.request({ url: '/Library/GetBook.php', qs: { ID: deletedBookId }, failOnStatusCode: false });
    }).then((lookupResponse) => {
      expect(lookupResponse.status, 'GetBook.php status after delete').to.eq(404);
      expect(lookupResponse.body.msg, 'msg').to.eq('The book by requested bookid / author name does not exists!');
    });
  });
});
