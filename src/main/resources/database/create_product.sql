create procedure create_product(IN product_name character varying, IN product_description character varying, IN product_price real)
    language plpgsql
as
$$
BEGIN
INSERT INTO product (name, description, price)
VALUES (product_name, product_description, product_price);
END;
$$;

alter procedure create_product(varchar, varchar, real) owner to postgres;

