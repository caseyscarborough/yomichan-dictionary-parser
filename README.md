# <img src="https://i.imgur.com/1QgctyK.png" height="24" alt="Yomichan Logo"> Yomichan Dictionary Parser

[![](https://github.com/caseyscarborough/yomichan-dictionary-parser/actions/workflows/gradle.yml/badge.svg)](https://github.com/caseyscarborough/yomichan-dictionary-parser/actions/workflows/gradle.yml)

This is a library that handles parsing the [Yomichan](https://github.com/FooSoft/yomichan/) dictionary format.

This library is created to simplify the process of using a Yomichan dictionary in a Java application.

The Yomichan dictionary format cannot be easily parsed in Java without manual parsing, because the JSON terms can use
arrays, objects, or strings for same keys, making it difficult to integrate with Java's type system (without using `Object`
everywhere and checking `instanceof` and casting).

This library was created based on the JSON schema definitions [here](https://github.com/FooSoft/yomichan/tree/master/ext/data/schemas).

## Requirements

- Java 17

## Installation

Add the dependency using JitPack:

```groovy
repositories {
    maven { url 'https://jitpack.io' }
}

dependencies {
    // Specific version
    implementation 'com.github.caseyscarborough:yomichan-dictionary-parser:<version>'
    
    // Master branch (latest)
    implementation 'com.github.caseyscarborough:yomichan-dictionary-parser:master-SNAPSHOT'
}
```

## Usage

### Parse a Dictionary File

You can parse a dictionary .zip file directly by passing the path to file, or the `File` object.

```java
YomichanParser parser = new YomichanParser();
YomichanDictionary dictionary = parser.parseDictionary("/path/to/yomichan/dictionary.zip");
```

This will return a `YomichanDictionary` object, which contains the object representation of the dictionary including the index, terms, and tags.

### Parse Extracted Dictionary Files

You can also individually parse the index, terms, and tags by passing the path (or `File` object) to the JSON file from the extracted dictionary.

```java
Index index = parser.parseIndex("/path/to/yomichan/index.json");
List<Term> terms = parser.parseTerms("/path/to/yomichan/term_bank_1.json");
List<Tag> tags = parser.parseTags("/path/to/yomichan/tag_bank_1.json");
List<Kanji> kanjis = parser.parseKanjis("/path/to/yomichan/kanji_bank_1.json");
```

### Using the `YomichanDictionary` Object

The `YomichanDictionary` object contains the index, terms, kanji, and tags from the dictionary.

```java
// The index parsed from the index.json file within the dictionary.
// Contains the metadata for the dictionary.
Index index = dictionary.getIndex();

// One of TERM, KANJI
YomichanDictionaryType type = dictionary.getType();

// The terms parsed from the term_bank.json files within the dictionary.
// This will be populated with then type is TERM
List<Term> terms = dictionary.getTerms();

// The kanji parsed from the kanji_bank.json files within the dictionary.
// This will be populated with the type is KANJI
List<Kanji> kanjis = dictionary.getKanjis();

// The tags parsed from the tag_bank.json files within the dictionary.
List<Tag> tags = dictionary.getTags();
```

### The `Index` Object

The index contains metadata about the dictionary such as the name, description, attribution, and version:

- Format - The version of the dictionary
- Version - The version of the dictionary (alias for format)
- Title - The title of the dictionary
- Description - The description of the dictionary
- Author - The author of the dictionary
- Attribution - Attribution information
- Url - URL for the source of the dictionary
- Revision - Revision of the dictionary
- Frequency Mode - `OCCURRENCE` or `RANK` based frequency mode

<details>
  <summary>Java Examples</summary>

```java
// The version of the dictionary (both methods return the version).
index.getFormat();
index.getVersion();
// The title and description of the dictionary.
index.getTitle();
index.getDescription();
// The author of the dictionary.
index.getAuthor();
// Attribution information.
index.getAttribution();
// URL for the source of the dictionary.
index.getUrl();
// Revision of the dictionary.
index.getRevision();
// OCCURRENCE or RANK based frequency mode.
Index.FrequencyMode mode = index.getFrequencyMode();
```

</details>

For more details and all functions, take a look at the [Yomichan Index JSON Schema](https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-index-schema.json)
or take a look at the [`Index` class](https://github.com/caseyscarborough/yomichan-dictionary-parser/blob/master/src/main/java/yomichan/model/Index.java).

### Working with Terms

The terms have been converted from their array format in the dictionary file to an object with the following properties:

- Term - The term itself, e.g. "読む"
- Reading - The reading of the term, e.g. "よむ"
- Definition Tags - Tags for the definitions, e.g. "v1", "vt"
- Term Tags - Tags for the entire term, e.g. "common"
- Score - Score used to determine popularity.
- Rules - String of space-separated rule identifiers for the definition which is used to validate delinflection, e.g. v1, v5, vs, adj-i
- Sequence Number - Sequence number for the term. Terms with the same sequence number are usually shown together.
- Contents - List of definitions for the term.

<details>
  <summary>Java Examples</summary>

```java
Term term = terms.get(0);
// The term itself, e.g. "読む"
String word = term.getTerm();
// The reading of the term, e.g. "よむ"    
String reading = term.getReading();
// Tags for the definitions, e.g. "v1", "vt"
List<String> definitionTags = term.getDefinitionTags();
// Tags for the entire term, e.g. "common"
List<String> termTags = term.getTermTags();
// Score used to determine popularity.
Integer score = term.getScore();
// String of space-separated rule identifiers for
// the definition which is used to validate delinflection
// e.g. v1, v5, vs, adj-i
List<String> rules = term.getRules();     
// Sequence number for the term. Terms with the
// same sequence number are usually shown together.
Integer sequence = term.getSequenceNumber();
// List of definitions for the term.
List<Content> contents = term.getContents();
```

</details>

The definitions (the `Content` list) can be in three separate formats, `TEXT`, `IMAGE`, or `STRUCTURED_CONTENT`.

`TEXT` definitions are simple and only contain a string of text for the definition:

```java
Content content = contents.get(0);
// The type of content, e.g. TEXT, IMAGE, STRUCTURED_CONTENT
ContentType type = content.getType();
// The text of the definition when the type is TEXT, e.g. "to read"
String text = content.getText();
```

The `STRUCTURED_CONTENT` type is a more complex definition that essentially maps to the structure
of specific HTML tags. This full structure from the Yomichan dictionary is retained in the Java object.

For example, it might be a `ul` or `table` type. Examples are shown below:

<details>
  <summary>Unordered List Example</summary>

```json
{
  "content": [
    {
      "text": "to read",
      "tag": "li"
    },
    {
      "text": "to decipher",
      "tag": "li"
    }
  ],
  "tag": "ul"
}
```

</details>


<details>
  <summary>Table Example</summary>

```json
{
  "content": [
    {
      "content": [
        {
          "text": "definition",
          "tag": "th"
        }
      ],
      "tag": "tr"
    },
    {
      "content": [
        {
          "text": "to read",
          "tag": "td"
        }
      ],
      "tag": "tr"
    }
  ],
  "tag": "table"
}
```

</details>

The structured content also has many additional properties on them such as styles (which map to CSS properties),
data (which map to `data` tags on the HTML entities), and language.

For more information take a look at the [Yomichan Term Bank v3 JSON Schema](https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-term-bank-v3-schema.json)
or the [`Term` class](https://github.com/caseyscarborough/yomichan-dictionary-parser/blob/master/src/main/java/yomichan/model/v3/Term.java) which is well documented.

### Working with Tags

Similar to terms, the tags have been converted from their array format in the dictionary file to an object,
but the structure is far simpler. Tags have the following:

- Name - The name of the tag
- Category - The category of the tag
- Order - The sorting order of the tag
- Notes - Notes for the tag
- Score - The score used to determine popularity. Negative values are more rare and positive values are more frequent. This score is also used to sort search results.

<details>
  <summary>Java Examples</summary>

```java
Tag tag = terms.get(0);
// The name of the tag.
String name = tag.getName();
// The category for th tag.
String category = tag.getCategory();
// Sorting order for the tag.
Integer order = tag.getOrder();
// Notes for the tag.
String notes = tag.getNotes();
// Score used to determine popularity. Negative values are more
// rare and positive values are more frequent. This score is
// also used to sort search results.
Integer score = tag.getScore();
```

</details>

For more information take a look at the [Yomichan Tag Bank v3 Schema](https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-tag-bank-v3-schema.json)
or the [`Tag` class](https://github.com/caseyscarborough/yomichan-dictionary-parser/blob/master/src/main/java/yomichan/model/v3/Tag.java) which is well documented.

### Working with Kanji

Kanji have the following fields:

- Character - The kanji character
- On'yomi - A list of on'yomi readings (in katakana)
- Kun'yomi - A list of kun'yomi readings (in hiragana)
- Meanings - A list of all meanings
- Tags - A list of tags for the kanji
- Stats - Key-value pairs of statistics for the kanji

<details>
  <summary>Java Examples</summary>

```java
Kanji kanji = kanjis.get(0);
// The kanji character
String character = kanji.getCharacter();
// A list of on'yomi readings (in katakana)
List<String> onyomi = kanji.getOnyomi();
// A list of kun'yomi readings (in hiragana)
List<String> kunyomi = kanji.getKunyomi();
// A list of all meanings
List<String> meanings = kanji.getMeanings();
// A list of tags for the kanji
List<String> tags = kanji.getTags();
// Key-value pairs of statistics for the kanji
Map<String, String> stats = kanji.getStats();
```

</details>

For more information take a look at the [Yomichan Kanji Bank v3 Schema](https://github.com/FooSoft/yomichan/blob/master/ext/data/schemas/dictionary-kanji-bank-v3-schema.json)
or the [`Kanji` class](https://github.com/caseyscarborough/yomichan-dictionary-parser/blob/master/src/main/java/yomichan/model/v3/Kanji.java).

## TODO

- [x] Implement `index.json` files
- [ ] Implement `term_bank.json` files for version 1
- [x] Implement `term_bank.json` files for version 3
- [x] Implement `tag_bank.json` files
- [ ] Implement `meta_bank.json` files
- [ ] Implement `kanji_bank.json` files for version 1
- [x] Implement `kanji_bank.json` files for version 3
- [ ] Implement `kanji_meta_bank.json` files for version 3
