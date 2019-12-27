db.createUser({
    user: "codeninja",
    pwd: "learnreactive",
    roles: [
        { role: "readWrite", db: "retail-dev" },
        { role: "readWrite", db: "retail-prod" }
    ]
})
