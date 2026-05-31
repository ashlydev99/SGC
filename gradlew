#!/bin/sh

APP_HOME=$(cd "$(dirname "$0")" && pwd) || exit

app_path=$0
while [ -h "$app_path" ]; do
    ls=$(ls -ld "$app_path")
    link=${ls#*' -> '}
    case $link in
        /*) app_path=$link ;;
        *) app_path=$(dirname "$app_path")/$link ;;
    esac
done

APP_HOME=$(cd "${app_path%/*}" && pwd -P) || exit
APP_BASE_NAME=${0##*/}

DEFAULT_JVM_OPTS='"-Xmx64m" "-Xms64m"'

MAX_FD=maximum

warn() {
    echo "$*"
} >&2

die() {
    echo
    echo "$*"
    echo
    exit 1
} >&2

cygwin=false
msys=false
darwin=false
nonstop=false
case "$(uname)" in
    CYGWIN*) cygwin=true ;;
    Darwin*) darwin=true ;;
    MSYS* | MINGW*) msys=true ;;
    NONSTOP*) nonstop=true ;;
esac

CLASSPATH=$APP_HOME/gradle/wrapper/gradle-wrapper.jar

if [ -n "$JAVA_HOME" ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
        JAVACMD=$JAVA_HOME/jre/sh/java
    else
        JAVACMD=$JAVA_HOME/bin/java
    fi
    if [ ! -x "$JAVACMD" ] ; then
        die "ERROR: JAVA_HOME is set to an invalid directory: $JAVA_HOME"
    fi
else
    JAVACMD=java
    if ! command -v java >/dev/null 2>&1
    then
        die "ERROR: JAVA_HOME is not set and no 'java' command could be found in your PATH."
    fi
fi

if ! "$cygwin" && ! "$darwin" && ! "$nonstop" ; then
    case $MAX_FD in
        max*)
            MAX_FD=$(ulimit -H -n)
            ;;
    esac
    case $MAX_FD in
        '' | soft) : ;;
        *)
            ulimit -n "$MAX_FD"
            ;;
    esac
fi

eval "set -- $DEFAULT_JVM_OPTS $JAVA_OPTS $GRADLE_OPTS \"\$@\""

exec "$JAVACMD" -classpath "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"