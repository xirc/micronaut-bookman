ALTER TABLE book DROP INDEX ix__book_updated_date;
ALTER TABLE person DROP INDEX ix__persons_updated_date;
CREATE INDEX ix__book_updated_date ON book(updated_date DESC);
CREATE INDEX ix__persons_updated_date ON person(updated_date DESC);