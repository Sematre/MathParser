# MathParser
[![Release Version][release-image]][release-url]
[![Maven Version][maven-image]][maven-url]
[![Build Status][travis-image]][travis-url]
[![License][license-image]][license-url]


A simple equation parser for Java.

## Code example

### Minimal
This code calculates the sum of "2+2". (= 4)
```java
String equation = "2+2";

MathParser mathParser = new MathParser();
System.out.println(mathParser.parse(equation));
```

### Advanced
Brackets? No problem! "18*(36-50)" = -252
```java
String equation = "18*(36-50)";

MathParser mathParser = new MathParser();
System.out.println(mathParser.parse(equation));
```

### Functions
You need functions? Too easy! "root(3,8)" (3th root of 8) = 2
```java
String equation = "root(3,8)";

MathParser mathParser = new MathParser();
System.out.println(mathParser.parse(equation));
```

### Custom functions
Not enough functions? Try this! fooBar(1,2,3) = 1 + 2 + 3 = 6
```java
String equation = "fooBar(1,2,3)";

MathParser mathParser = new MathParser();
mathParser.addFunction("fooBar", new MathFunction() {

	@Override
	public Double execute(Double[] parameter) {
		return parameter[0] + parameter[1] + parameter[2];
	}
});

System.out.println(mathParser.parse(equation));
```

### CrAZy foRMAtTING
Still no problem! 5((2+4)-2)+sin(root(1)*pi) = 20
```java
String equation = "((  (5*     ((2   +4)-2)   +sin(root(2,1)*pi())))";

MathParser mathParser = new MathParser();
System.out.println(mathParser.parse(equation));
```

## Implementation
Gradle:
```gradle
dependencies {
	implementation 'de.sematre.mathparser:MathParser:1.0'
}
```

Maven:
```xml
<dependency>
	<groupId>de.sematre.mathparser</groupId>
	<artifactId>MathParser</artifactId>
	<version>1.0</version>
</dependency>
```

## Release History
* 1.0
    * Initial version

## Info
© Sematre 2019

Distributed under the **MIT License**. See ``LICENSE`` for more information.

[release-image]: https://img.shields.io/github/release/Sematre/MathParser.svg?style=flat-square
[release-url]: https://github.com/Sematre/MathParser/releases/latest

[maven-image]: https://img.shields.io/maven-central/v/de.sematre.mathparser/MathParser.svg?style=flat-square
[maven-url]: https://search.maven.org/artifact/de.sematre.mathparser/MathParser/

[travis-image]: https://img.shields.io/travis/com/Sematre/MathParser.svg?style=flat-square
[travis-url]: https://travis-ci.com/Sematre/MathParser

[license-image]: https://img.shields.io/github/license/Sematre/MathParser.svg?style=flat-square
[license-url]: https://github.com/Sematre/MathParser/blob/master/LICENSE