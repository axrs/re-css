module.exports = config => config.set({
  basePath: 'target/karma',
  files: ['test.js'],
  browsers: ['CrackChromiumHeadless'],
  customLaunchers: {
    CrackChromiumHeadless: {
      base: 'ChromiumHeadless',
      displayName: 'ChromiumHeadless',
      flags: [
        '--disable-dev-shm-usage',
        '--disable-gpu',
        '--disk-cache-size=0',
        '--headless'
      ]
    }
  },
  frameworks: ['cljs-test'],
  plugins: ['karma-cljs-test', 'karma-chrome-launcher'],
  colors: true,
  logLevel: config.LOG_INFO,
  client: {
    args: ['shadow.test.karma.init'],
    singleRun: true
  },
  singleRun: true,
  retryLimit: 0
})
