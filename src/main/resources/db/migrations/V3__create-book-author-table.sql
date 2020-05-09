CREATE TABLE book_author
(
    book_id VARCHAR(36) PRIMARY KEY,
    person_id VARCHAR(36),
    CONSTRAINT fk__book_author_book_id FOREIGN KEY (book_id) REFERENCES book(id) ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT fk__book_author_person_id FOREIGN KEY (person_id) REFERENCES person(id) ON UPDATE RESTRICT ON DELETE RESTRICT
);