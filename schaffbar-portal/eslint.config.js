// @ts-check
const eslint = require('@eslint/js');
const tseslint = require('typescript-eslint');
const angular = require('angular-eslint');
const eslintPluginPrettierRecommended = require('eslint-plugin-prettier/recommended');
const importPlugin = require('eslint-plugin-import');

module.exports = tseslint.config(
  {
    files: ['**/*.ts'],
    extends: [
      eslint.configs.recommended,
      ...tseslint.configs.recommended,
      ...tseslint.configs.stylistic,
      ...angular.configs.tsRecommended,
      eslintPluginPrettierRecommended,
      // Extends two more configuration from "import" plugin
      importPlugin.flatConfigs?.recommended,
      importPlugin.flatConfigs?.typescript,
    ],
    processor: angular.processInlineTemplates,
    rules: {
      '@angular-eslint/directive-selector': [
        'error',
        {
          type: 'attribute',
          prefix: 'schbar',
          style: 'camelCase',
        },
      ],
      '@angular-eslint/component-selector': [
        'error',
        {
          type: 'element',
          prefix: 'schbar',
          style: 'kebab-case',
        },
      ],
      // import rules
      'import/no-unresolved': 'error',
      'import/order': [
        'error',
        {
          'newlines-between': 'always',
          // Valid values: ("builtin" | "external" | "internal" | "unknown" | "parent" | "sibling" | "index" | "object" | "type")[]
          groups: [
            'internal', // angular imports - configured in 'import/internal-regex'
            'unknown', // angular material imports
            'external', // all libraries imports - configured in 'import/external-module-folders'
            'builtin', // internal-library imports
            ['parent', 'sibling', 'index'], // relative paths
          ],
          pathGroups: [
            {
              pattern: '@angular/material/**',
              group: 'unknown',
              position: 'after',
            },
            {
              pattern: '@internal-library', // TODO: change it to your internal library
              group: 'builtin',
            },
          ],
          pathGroupsExcludedImportTypes: ['type', 'object'],
        },
      ],
    },
    settings: {
      'import/internal-regex': '^@angular/',
      'import/external-module-folders': ['node_modules'],
      'import/parsers': {
        '@typescript-eslint/parser': ['.ts'],
      },
      'import/resolver': {
        alias: true, // <-- Added this line to handle TS aliases
        typescript: {
          alwaysTryTypes: true,
          project: ['tsconfig.json'],
        },
        node: {
          extensions: ['.ts'],
        },
      },
    },
  },
  {
    files: ['**/*.html'],
    extends: [...angular.configs.templateRecommended, ...angular.configs.templateAccessibility],
    rules: {},
  },
);
