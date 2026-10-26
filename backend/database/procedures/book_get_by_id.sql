-- Reference script. The matching Flyway migration is authoritative.
CREATE OR ALTER PROCEDURE dbo.Book_GetById
    @id BIGINT
AS
BEGIN
    SET NOCOUNT ON;
    SELECT id, title, description, stock, is_shown, price
    FROM dbo.books
    WHERE id = @id;
END;
