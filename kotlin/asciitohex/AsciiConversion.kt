package asciitohex

enum class AsciiConversion {
    UTF8,
    Binary,
    Hexadecimal,
    Decimal,
    Base64,
    SHA1,
    MD5;
    
    fun readOnly() = this in listOf(SHA1, MD5)
}
