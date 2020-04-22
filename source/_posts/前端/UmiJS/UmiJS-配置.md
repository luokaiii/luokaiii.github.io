---
title: 《UmiJS》官网笔记
date: 2020-04-02 10:00:00
categories:
  - 前端
  - UmiJS
tags:
  - UmiJS
  - hide
---
# UmiJS 配置

```ts
export default {
  /**
   * alias: 配置别名，对应用路径进行隐射。
   * Umi 内置了以下别名：
   * - @，项目的 src 目录
   * - @@，临时目录，通常是 src/.umi 目录
   * - umi，当前所运行的 umi 仓库目录
   * - react-router 和 react-router-dom，底层路由库，锁定版本
   * - react 和 react-dom，默认使用 16.x 版本，但会优先使用项目中依赖的版本
   */
  alias: {
    // 在使用时， `import('foo')`，实际上就是 `import('/tmp/a/b/foo')`
    'foo': '/tmp/a/b/foo'
  },
  analyze: {
    /**
     * analyze: 包模块结构分析工具，可看到各项目各模块大小，按需优化。通过 `ANALYZE=1 umi build/dev` 开启，默认server端口号`8888`
     */
    analyzerMode: 'server',
    analyzerPort: 8888,
    openAnalyzer: true,
    generateStatsFile: false,
    statsFilename: 'stats.json',
    logLevel: 'info',
    defaultSizes: 'parsed' // stat // gzip
  },
  /**
   * autoprefixer：设置 autoprefixer 的配置项
   * 注：`overrideBrowsersList` 被内部接管，不要设置，可通过 target 配置来选择要兼容的浏览器
   */
  autoprefixer: {
    flexbox: 'no-2009'
  },
  /**
   * base：设置路由前缀，通常用于部署到非根目录
   * Type：string，Default：/
   */
  base: '/',
  /**
   * chainWebpack：通过 webpack-chain 的API修改 webpack 配置
   * - memo，当前 webpack-chain 对象
   * - env，当前环境，development、production或test等
   * - webpack，webpack实例，用于获取内部插件
   * - createCSSRule，用于扩展其他CSS实现，如sass，stylus
   */
  chainWebpack(memo, {env,webpack,createCSSRule}) {
    // 设置 alias
    memo.resolve.alias.set('foo', '/tmp/a/b/foo');

    // 删除 umi 内置插件
    memo.plugins.delete('progress');
    memo.plugins.delete('friendly-error');
    memo.plugins.delete('copy');
  },
  /**
   * chunks：默认是 `['umi']`
   */
  chunks: ['umi'],
  /**
   * 配置css-loader
   */
  cssLoader: {},
  /**
   * cssModulesTypescriptLoader：对按照css modules 方式引入的css或less文件，自动生成ts类型定义文件
   * Default：undefined
   */
  cssModulesTypescriptLoader: {
    mode: 'emit'
  },
  /**
   * 设置 cssnano 配置项，基于default 的配置集合
   */
  cssnano: {
    normalizeUrl: false
  },
  /**
   * copy：设置要复制到输出目录的文件或文件夹
   */
  copy: [],
  /**
   * define：用于提供给代码中可用的变量
   */
  define: {
    // log(FOO) 会被编译成 log('bar')
    FOO: 'bar'
  },
  /**
   * devServer：配置 开发服务器
   */
  devServer: {
    port: 8888,
    host: 0.0.0.0,
    https: true,
    http2: true
  },
  /**
   * devtool：用户配置 sourcemap 类型
   * Default：cheap-module-source-map(dev)/false(build)
   * 常见可选值：
   * - eval，最快的类型，但不支持低版本浏览器
   * - source-map，最慢最全的类型
   */
  devtool: 'eval',

  /**
   * dynamicImport：是否启用按需加载
   * Default：false，只会生成一个js和css
   */
  dynamicImport: {
    // 子配置项，指定loading组件文件
    loading: '@/Loading'
  },
  /**
   * exportStatic，配置 html 输出形式
   * 默认只输出 index.html
   * 开启后，会针对每个路由输出html文件
   * - htmlSuffix，启用 .html 后缀
   * - dynamicRoot，部署到任意路径
   */
  exportStatic: {
    htmlSuffix: true
  },
  /**
   * externals：设置哪些模块可以不被打包，通过 <script> 或其他方式引入，通常需要和 scripts 或 headScripts 配置同时使用
   * 即 `import react from 'react'` 会被替换成 `const react = window.React`
   */
  externals: {
    react: 'window.React'
  },
  scripts: [
    'https://unpkg.com/browse/react@16.12.0/umd/react.production.min.js'
  ],
  /**
   * extraBabelPlugins：配置额外的 babel 插件
   */
  extraBabelPlugins: [
    'babel-plugin-react-require'
  ],
  /**
   * extraBabelPresets：配置额外的 babel 插件集
   */
  extraBabelPresets: [],
  /**
   * extraPostCSSPlugins：配置额外的 postcss 插件
   */
  extraPostCSSPlugins: [],
  /**
   * favicon：配置 favicon 地址
   * 本地图片，请放到 public 目录
   */
  favicon: '/assets/favicon.ico',
  /**
   * forkTSCheker：开启Typescript编译时类型检查
   * 默认关闭
   */
  forkTSCheker: {},
  /**
   * hash：是否让生成的文件包含hash后缀
   * 通常用于增量发布和避免浏览器加载缓存
   * 注：html文件始终没有hash
   */
  hash: false,
  /**
   * headScripts：配置head里的额外脚本
   */
  headScripts: [],
  /**
   * history：配置history
   * - type，可选browser、hash、memory
   */
  history: {},
  /**
   * ignoreMomentLocale：忽略 moment 的 locale 文件，用于减少尺寸
   */
  ignoreMomentLocale: false,
  /**
   * inlineLimit：配置图片文件是否走 base64 编译的阈值。大于配置项(默认10k)的图片，会被单独生成为文件
   */
  inlineLimit: 10000,
  /**
   * lessLoader：配置less-loader
   */
  lessLoader: {},
  /**
   * links：配置额外的link标签
   */
  links: [],
  /**
   * manifest：是否需要生成额外的manifest文件，默认生成asset-manifest.json
   * - fileName，文件名 'asset-manifest.json',
   * - publicPath，默认为webpack的output.publicPath
   * - basePath，给所有文件路径加前缀
   */
  manifest: {},
  /**
   * metas：配置额外的 mata标签
   */
  metas: [],
  /**
   * mock：配置mock属性
   * - exclude，忽略不需要走mock的文件
   */
  mock: {
    exclude: []
  },
  /**
   * mountElementId：指定react渲染到html的id
   */
  mountElementId: 'root',
  /**
   * mpa：切换渲染模式为 mpa
   * 特性：
   * - 为每个页面输出html
   * - 输出不包含 react-router、react-router-dom、history 等库
   * - 渲染和url解绑，html放哪都能使用
   * 注意：
   * - 只支持一级路由配置
   * - 不支持layout或嵌套路由配置
   */
  npa:{},
  /**
   * nodeModulesTransform：设置node_modules 目录下依赖文件的编译方式
   * - type，可选all或none
   *  - all：全部编译，通过exclude忽略
   *  - none：全不编译，通过exclude包含
   * - exclude，忽略的依赖库、包名，不支持绝对路径
   */
  nodeModulesTransform: {
    type: 'all'
  },
  /**
   * outputPath：指定输出路径
   * 不能为约定目录
   */
  outputPath: 'dist',
  /**
   * plugins: 配置额外的umi插件
   */
  plugins: [
    // npm依赖
    'umi-plugin-hello',
    // 相对路径，从根目录开始找
    './plugin',
    // 绝对路径
    `${__dirname}/plugin.js`
  ],
  /**
   * polyfill：设置按需引入polyfill
   * 默认全量引入
   */
  polyfill: {
    imports: []
  },
  /**
   * postcssLoader：配置 postcss-loader
   */
  postcssLoader: {},
  /**
   * proxy：配置代理能力
   */
  proxy: {
    '/api': {
      'target': 'http://jsonplaceholder.typicode.com/',
      'changeOrigin': true,
      'pathRewrite': {
        '^/api': ''
      }
    }
  },
  /**
   * publicPath
   */
  publicPath: '/',
  /**
   * routes：配置路由
   * umi 的路由基于 react-router@5 实现
   */
  routes: [
    {
      path: '/',
      component: '@/pages/layout/index',
      routes: [
        { path: '/home', component: '@/pages/home' },
        { path: '/faq', component: '@/pages/faq' },
        { path: '/me', component: '@/pages/me' },
      ],
    },
  ],
  /**
   * runtimePublicPath：配置启用运行时 publicPath
   */
  runtimePublicPath: false,
  /**
   * scripts：同headScripts，配置在body里的脚本
   */
  scripts:[],
  /**
   * singular：配置是否启用单数模式的目录
   */
  singular: false,
  /**
   * styleLoader：启用并设置 style-loader配置
   * 让css内联打包在js中，不输出额外的css文件
   */
  styleLoader: {},
  /**
   * styles：配置额外的css
   */
  styles: [
    `body: {color: red}`,
    `https://a.com/b.css`
  ],
  /**
   * targets：配置需要兼容的浏览器最低版本
   * 自动引入polyfill和语法转换
   */
  targets: {
    chrome: 49, 
    firefox: 64, 
    safari: 10, 
    edge: 13, 
    ios: 10
  },
  /**
   * theme：配置主题，实际上是配less变量
   */
  theme: {
    '@primary-color':'#1DA57A'
  },
  /**
   * title：配置标题
   * 还可以针对路由配置标题
   * 注意：
   * - 默认不会在HTML里输出title标签，而是通过动态渲染得到
   * - 配置 exportStatic 后会为每个HTML输出title标签
   * - 如果需要自行渲染title，可配置title：false，禁用内置title渲染机制
   */
  title: 'hi',
}
```