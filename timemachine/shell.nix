{ pkgs ? import <nixpkgs> {} }:
pkgs.mkShell {
  name = "uv-shell";
  buildInputs = with pkgs; [
	uv
  ];
}
