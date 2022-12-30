export JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk-11.0.14.jdk/Contents/Home/


mvn --projects cerca --also-make clean package
[ -f "cerca/target/cerca-0.0.1.jar" ] && cp cerca/target/cerca-0.0.1.jar ./bins/cerca.jar && git commit -m "project built" && git push &&  git status
