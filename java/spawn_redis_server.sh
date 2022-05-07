#!/bin/sh
set -e
tmpFile=$(mktemp -d)
javac -sourcepath src/main/java src/main/java/io/github/benrkia/redis/Main.java -d "$tmpFile"
jar cf java_redis.jar -C "$tmpFile"/ .
rm -r "$tmpFile"
exec java -cp java_redis.jar io.github.benrkia.redis.Main "$@"