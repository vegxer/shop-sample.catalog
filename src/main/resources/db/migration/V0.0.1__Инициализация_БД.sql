CREATE TABLE public.attachment (
    id bigint NOT NULL
        CONSTRAINT attachment_pk PRIMARY KEY,
    path character varying(256),
    thumbnail_path character varying(256)
);

ALTER TABLE public.attachment
    OWNER TO postgres;

CREATE SEQUENCE public.attachment_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1
    OWNED BY public.attachment.id;

ALTER SEQUENCE public.attachment_id_seq
    OWNER TO postgres;

CREATE TABLE public.category (
    id bigint NOT NULL
        CONSTRAINT category_pk PRIMARY KEY,
    name character varying(256) NOT NULL,
    attachment_id bigint
        CONSTRAINT category_attachment_fk REFERENCES attachment (id)
        ON UPDATE CASCADE
        ON DELETE SET NULL,
    parent_id bigint
        CONSTRAINT category_parent_fk REFERENCES category (id)
        ON UPDATE CASCADE
        ON DELETE SET NULL
);

ALTER TABLE public.category
    OWNER TO postgres;

CREATE SEQUENCE public.category_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1
    OWNED BY public.category.id;

ALTER SEQUENCE public.category_id_seq
    OWNER TO postgres;

CREATE TABLE public.product (
    id bigint NOT NULL
        CONSTRAINT product_pk PRIMARY KEY,
    name character varying(256) NOT NULL,
    description character varying(256),
    price numeric(19, 2) NOT NULL,
    amount bigint NOT NULL,
    state integer,
    category_id bigint NOT NULL
        CONSTRAINT product_category_fk REFERENCES category (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

ALTER TABLE public.product
    OWNER TO postgres;

CREATE SEQUENCE public.product_id_seq
    INCREMENT 1
    START 1
    MINVALUE 1
    MAXVALUE 9223372036854775807
    CACHE 1
    OWNED BY public.product.id;

ALTER SEQUENCE public.product_id_seq
    OWNER TO postgres;

CREATE TABLE public.product_attachment (
    product_id bigint NOT NULL
        CONSTRAINT product_attachment_product_id_fk REFERENCES product (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    attachment_id bigint NOT NULL
        CONSTRAINT product_attachment_attachment_id_fk REFERENCES attachment (id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

ALTER TABLE public.product_attachment
    OWNER TO postgres;
