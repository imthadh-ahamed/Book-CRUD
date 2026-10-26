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
    FROM dbo.books WHERE id = @new_id;
END;
GO

CREATE OR ALTER PROCEDURE dbo.Book_GetById @id BIGINT
AS
BEGIN
    SET NOCOUNT ON;
    SELECT id, title, description, stock, is_shown, price
    FROM dbo.books WHERE id = @id;
END;
GO

CREATE OR ALTER PROCEDURE dbo.Book_GetAll
AS
BEGIN
    SET NOCOUNT ON;
    SELECT id, title, description, stock, is_shown, price
    FROM dbo.books ORDER BY id ASC;
END;
GO

CREATE OR ALTER PROCEDURE dbo.Book_Update
    @id BIGINT,
    @title NVARCHAR(200),
    @description NVARCHAR(2000) = NULL,
    @stock NVARCHAR(20),
    @is_shown BIT,
    @price DECIMAL(12,2)
AS
BEGIN
    SET NOCOUNT ON;
    UPDATE dbo.books
    SET title = @title, description = @description, stock = @stock,
        is_shown = @is_shown, price = @price
    WHERE id = @id;

    IF @@ROWCOUNT = 0
    BEGIN
        SELECT CAST(NULL AS BIGINT) AS id,
               CAST(NULL AS NVARCHAR(200)) AS title,
               CAST(NULL AS NVARCHAR(2000)) AS description,
               CAST(NULL AS NVARCHAR(20)) AS stock,
               CAST(NULL AS BIT) AS is_shown,
               CAST(NULL AS DECIMAL(12,2)) AS price
        WHERE 1 = 0;
        RETURN;
    END;

    SELECT id, title, description, stock, is_shown, price
    FROM dbo.books WHERE id = @id;
END;
GO

CREATE OR ALTER PROCEDURE dbo.Book_Delete @id BIGINT
AS
BEGIN
    SET NOCOUNT ON;
    DELETE FROM dbo.books WHERE id = @id;
    SELECT @@ROWCOUNT AS rows_affected;
END;
GO
