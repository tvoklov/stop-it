const path = require('path');

module.exports = {
  entry: {
    app: './src/App.js'
  },
  output: {
    path: path.resolve(__dirname, '../src/main/resources/public/js'),
    filename: 'App.js',
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        exclude: /(node_modules)/,
        loader: 'babel-loader',
        options: {
          presets: ['@babel/preset-env', '@babel/preset-react']
        }
      }
    ]
  }
};