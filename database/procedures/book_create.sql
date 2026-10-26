-- Reference script. The matching Flyway migration is authoritative.
CREATE OR ALTER PROCEDURE dbo.Book_Create
    @title NVARCHAR(200),
    @description NVARCHAR(2000) = NULL,
    @stock NVARCHAR(20),
    @is_shown BIT,
    @price DECIMAL(12,2)
AS
BEGIN
    SET NOCOUNT ON;

    INSERT INTO dbo.books (title, description, stock, is_shown, price)
    VALUES (@title, @description, @stock, @is_shown, @price);

    DECLARE @new_id BIGINT = CONVERT(BIGINT, SCOPE_IDENTITY());

    SELECT id, title, description, stock, is_shown, price
    FROM dbo.books
    WHERE id = @new_id;
END;
