-- Las publicaciones de la comunidad admiten también videos de Facebook.
ALTER TABLE isadecor_publicaciones DROP CONSTRAINT chk_isadecor_publicaciones_red;
ALTER TABLE isadecor_publicaciones ADD CONSTRAINT chk_isadecor_publicaciones_red CHECK (red IN ('INSTAGRAM', 'TIKTOK', 'FACEBOOK'));
