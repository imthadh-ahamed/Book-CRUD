-- Reference script. The matching Flyway migration is authoritative.
CREATE OR ALTER PROCEDURE dbo.Book_GetAll
AS
BEGIN
    SET NOCOUNT ON;
    SELECT id, title, description, stock, is_shown, price
    FROM dbo.books
    ORDER BY id ASC;
END;
