config.devServer = config.devServer || {}
config.devServer.port = 8081
config.devServer.open = false
config.devServer.watchOptions = {
    "aggregateTimeout": 1000,
    "poll": 1000
}