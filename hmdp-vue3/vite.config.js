import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
// import vueDevTools from 'vite-plugin-vue-devtools'
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // vueDevTools(),
    AutoImport({
      resolvers: [ElementPlusResolver()]
    }),
    Components({
      resolvers: [ElementPlusResolver()]
    })
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    }
  },
  // //Configure development server related options
  // server: {
  //   // Automatically open the browser
  //   open: true,
  //   //Specify the port number the server is running on as 1010
  //   // port: 1010,
  //   // Enable hot module replacement (Hot Module Replacement, HMR)
  //   // During the development process, when modifying the code, the browser can update in real time without completely refreshing the page.
  //   hmr: true,
  //   proxy: {
  //     '/api': {
  //       // Target server address, here is another local service running on port 1011
  //       target: 'http://localhost:8085',
  //       // Whether to change the source of the request (Origin). When set to true, the source of the request will be modified to the source of the target server.
  //       changeOrigin: true,
  //       // Path rewriting rules, replace paths starting with /api with empty strings
  //       // For example, requests for /api/users will be forwarded to the /users path of the target server
  //       rewrite: (path) => path.replace(/^\/api/, '')
  //       // Another common path rewriting method, the effect is the same as the rewrite function above
  //       // "^/api": "",
  //     }
  //   }
  // },
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8085',
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, '')
      }
    }
  }
})
