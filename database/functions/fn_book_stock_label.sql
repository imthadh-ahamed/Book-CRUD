-- Optional reference example. Only deploy if the application actually needs it.
-- Keep synchronized with a corresponding Flyway migration if retained.
CREATE OR ALTER FUNCTION dbo.fn_book_stock_label (@stock NVARCHAR(20))
RETURNS NVARCHAR(40)
AS
BEGIN
    RETURN CASE @stock
        WHEN 'AVAILABLE' THEN 'Available'
        WHEN 'BOOKED' THEN 'Booked'
        WHEN 'OUT_OF_STOCK' THEN 'Out of stock'
        ELSE 'Unknown'
    END;
END;
