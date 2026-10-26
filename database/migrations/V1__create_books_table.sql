CREATE TABLE dbo.books (
    id BIGINT IDENTITY(1,1) NOT NULL CONSTRAINT PK_books PRIMARY KEY,
    title NVARCHAR(200) NOT NULL,
    description NVARCHAR(2000) NULL,
    stock NVARCHAR(20) NOT NULL,
    is_shown BIT NOT NULL CONSTRAINT DF_books_is_shown DEFAULT (1),
    price DECIMAL(12,2) NOT NULL,
    CONSTRAINT CK_books_stock CHECK (stock IN ('AVAILABLE', 'BOOKED', 'OUT_OF_STOCK')),
    CONSTRAINT CK_books_price_nonnegative CHECK (price >= 0)
);
