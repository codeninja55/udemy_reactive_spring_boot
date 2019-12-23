db.createUser({
    user: "codeninja",
    pwd: "learnreactive",
    roles: [
        { role: "readWrite", db: "reactive" }
    ]
})
