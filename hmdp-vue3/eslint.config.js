import { defineConfig, globalIgnores } from 'eslint/config'
import globals from 'globals'
import js from '@eslint/js'
import pluginVue from 'eslint-plugin-vue'
import skipFormatting from '@vue/eslint-config-prettier/skip-formatting'
import eslintPluginPrettierRecommended from 'eslint-plugin-prettier/recommended'

export default defineConfig([
  {
    name: 'app/files-to-lint',
    files: ['**/*.{js,mjs,jsx,vue}']
  },

  globalIgnores(['**/dist/**', '**/dist-ssr/**', '**/coverage/**']),

  {
    languageOptions: {
      globals: {
        ...globals.browser,
        ElMessage: 'readonly',
        ElMessageBox: 'readonly',
        ElLoading: 'readonly'
      }
    }
  },
  {
    rules: {
      // Use the rules provided by the plug-in (format: `plug-in name/rule name`)
      'vue/multi-word-component-names': [
        'warn',
        {
          ignores: ['index'] // vue组件名称多单词组成（忽略index.vue）
        }
      ],
      'vue/no-setup-props-destructure': ['off'], // 关闭 props 解构的校验
      // 💡 Add undefined variable error message, create-vue@3.6.3 is closed, added here to support the next chapter demonstration.
      'no-undef': 'off',
      // Add new rule
      'space-before-function-paren': ['error', 'always'] // ← 添加在此处
      // //Disable automatic import rules
      // 'import/named': 'off',
      // 'import/namespace': 'off',
      // // Disable automatic deletion of unused variables (if not needed)
      // 'no-unused-vars': 'off',
      // // If you use TypeScript related rules, they also need to be disabled
      // '@typescript-eslint/no-unused-vars': 'off'
    }
  },
  js.configs.recommended,
  ...pluginVue.configs['flat/essential'],
  skipFormatting,
  eslintPluginPrettierRecommended
])
