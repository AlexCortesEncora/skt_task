create procedure select_products(INOUT result refcursor)
    language plpgsql
as
$$
BEGIN
open result for SELECT * from product;
END;
$$;

alter procedure select_products(inout refcursor) owner to postgres;

