# Setup

## Upgrade/Update Angular CLI

```bash
npm install -g @angular/cli@latest
ng version
```

## Create new project with Angular Material

```bash
ng new schaffbar-portal
SCSS
no
```

```bash
ng add @angular/material
```

## Install ESLint and Prettier

see: [ESLint and Prettier](https://senoritadeveloper.medium.com/setting-up-eslint-and-prettier-in-angular-with-vs-code-and-webstorm-4be8d558caea)

```bash
ng add @angular-eslint/schematics
```

```bash
npm install prettier --save-dev
```

.prettierrc

```json
{
  "singleQuote": true,
  "printWidth": 120,
  "semi": true,
  "bracketSameLine": true,
  "tabWidth": 2
}
```

.prettierignore

```
# Add files here to ignore them from prettier formatting
.angular

/dist
/node_modules
```

## Integrating Prettier with ESLint

see: [ESLint and Prettier](https://senoritadeveloper.medium.com/setting-up-eslint-and-prettier-in-angular-with-vs-code-and-webstorm-4be8d558caea)

```bash
npm install eslint-config-prettier --save-dev
npm install eslint-plugin-prettier --save-dev
```

Edit eslint.config.js

```js
// @ts-check
const eslint = require('@eslint/js');
const tseslint = require('typescript-eslint');
const angular = require('angular-eslint');
const eslintPluginPrettierRecommended = require('eslint-plugin-prettier/recommended');  <-- add this line

module.exports = tseslint.config(
  {
    files: ['**/*.ts'],
    extends: [
      eslint.configs.recommended,
      ...tseslint.configs.recommended,
      ...tseslint.configs.stylistic,
      ...angular.configs.tsRecommended,
      eslintPluginPrettierRecommended, <-- add this line
    ],
```

## Adapt Selector Prefix

Edit angular.json

```json
      ...
      "root": "",
      "sourceRoot": "src",
      "prefix": "schbar", <-- change prefix
      "architect": {
        "build": {
      ...
```

Edit eslint.config.js

```js
    rules: {
      '@angular-eslint/directive-selector': [
        'error',
        {
          type: 'attribute',
          prefix: 'schbar', <-- change prefix
          style: 'camelCase',
        },
      ],
      '@angular-eslint/component-selector': [
        'error',
        {
          type: 'element',
          prefix: 'schbar', <-- change prefix
          style: 'kebab-case',
        },
      ],
    },
```

## Visual Studio Code Settings

.vscode/settings.json

```json
{
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode",
  "editor.codeActionsOnSave": {
    "source.fixAll.ts": "always",
    "source.fixAll.eslint": "always",
    "source.organizeImports": "always"
  },
  "eslint.validate": ["typescript"],
  "prettier.configPath": ".prettierrc",
  "[typescript]": {
    "editor.defaultFormatter": "esbenp.prettier-vscode"
  }
}
```

.vscode/extensions.json

```json
{
  // For more information, visit: https://go.microsoft.com/fwlink/?linkid=827846
  "recommendations": ["angular.ng-template", "esbenp.prettier-vscode", "dbaeumer.vscode-eslint"]
}
```

## Setup import order

Install dependencies

```bash
npm install eslint-plugin-import --save-dev
npm install eslint-import-resolver-typescript --save-dev
npm install eslint-import-resolver-alias --save-dev
```

Adapt eslint.config.js

```js
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
      importPlugin.flatConfigs?.recommended,      <-- add this line
      importPlugin.flatConfigs?.typescript,       <-- add this line
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
      'import/no-unresolved': 'error',             <-- add lines from here
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
    },                                            <-- add lines from here
  },
  {
    files: ['**/*.html'],
    extends: [...angular.configs.templateRecommended, ...angular.configs.templateAccessibility],
    rules: {},
  },
);
```

# Development

## Local server

```bash
ng serve
```

Once the server is running, open your browser and navigate to `http://localhost:4200/`.

## Code scaffolding

Angular CLI includes powerful code scaffolding tools. To generate a new component, run:

```bash
ng generate component component-name
ng generate component users/components/new-user-form --dry-run
```

For a complete list of available schematics (such as `components`, `directives`, or `pipes`), run:

```bash
ng generate --help
```

## Upgrade project dependencies

```bash
ng update @angular/core @angular/cli
ng update @angular/material
...
```
