ALTER TABLE book_author DROP FOREIGN KEY fk__book_author_book_id;
ALTER TABLE book_author DROP FOREIGN KEY fk__book_author_person_id;
ALTER TABLE book_author DROP PRIMARY KEY;
ALTER TABLE book_author ADD PRIMARY KEY (book_id, person_id);
ALTER TABLE book_author
    ADD CONSTRAINT fk__book_author_book_id FOREIGN KEY (book_id) REFERENCES book(id) ON UPDATE CASCADE ON DELETE CASCADE;
ALTER TABLE book_author
    ADD CONSTRAINT fk__book_author_person_id FOREIGN KEY (person_id) REFERENCES person(id) ON UPDATE CASCADE ON DELETE CASCADE;