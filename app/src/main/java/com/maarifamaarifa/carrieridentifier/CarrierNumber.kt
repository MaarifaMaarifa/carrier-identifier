package com.maarifamaarifa.carrieridentifier

enum class Carrier {
    VODACOM {
        override fun toString(): String {
            return "Vodacom"
        }
            },
    TIGO {
        override fun toString(): String {
            return "Tigo"
        }
         },
    AIRTEL {
        override fun toString(): String {
            return "Airtel"
        }
           },
    ZANTEL {
        override fun toString(): String {
            return "Zantel"
        }
           },
    HALOTEL {
            override fun toString(): String {
                return "Halotel"
            }
            },
    TTCL,
}

enum class NumberType {
    WITH_COUNTRY_CODE,
    WITH_COUNTRY_CODE_AND_PLUS,
    WITHOUT_COUNTRY_CODE;

    companion object {
        fun new(numberStr: String): NumberType {
            return when {
                numberStr.startsWith("+255") -> WITH_COUNTRY_CODE_AND_PLUS
                numberStr.startsWith("255") -> WITH_COUNTRY_CODE
                numberStr.startsWith("0") -> WITHOUT_COUNTRY_CODE
                else -> throw InvalidNumberType()

            }

        }

        fun stripTypeSignature(numberStr: String, numberType: NumberType): String {
            return when (numberType) {
                WITH_COUNTRY_CODE -> numberStr.slice(3..<numberStr.length)
                WITH_COUNTRY_CODE_AND_PLUS -> numberStr.slice(4..<numberStr.length)
                WITHOUT_COUNTRY_CODE -> numberStr.slice(1..<numberStr.length)
            }
        }
    }
}

class IdentifiedNumber(strippedNumberStr: String) {
    val numberStr: String
    val carrier: Carrier

    init {
        if (strippedNumberStr.length < 2) {
            throw NumberTooShort()
        }
        val carrier = when(strippedNumberStr[0]) {
            '7' -> when(strippedNumberStr[1])  {
                '4', '5', '6' -> Carrier.VODACOM
                '1' -> Carrier.TIGO
                '8' -> Carrier.AIRTEL
                '3' -> Carrier.TTCL
                '7' -> Carrier.ZANTEL
                else -> throw UnknownCarrier()
            }
            '6' -> when(strippedNumberStr[1]) {
                '5', '7', '1' -> Carrier.TIGO
                '8', '9' -> Carrier.AIRTEL
                '2' -> Carrier.HALOTEL
                else -> throw UnknownCarrier()
            }
            else -> throw UnknownCarrier()
        }
        this.carrier = carrier
        this.numberStr = "0${strippedNumberStr}"
    }

    fun verifyLength() {
        if (numberStr.length > 10) {
            throw NumberTooLong()
        } else if (numberStr.length < 10) {
            throw NumberTooShort()
        }
    }
}

class RawNumber(private val numberStr: String) {
    fun identifyNumber(): IdentifiedNumber {
        val numberType = NumberType.new(numberStr)
        val strippedNumberStr = NumberType.stripTypeSignature(numberStr, numberType)
        return IdentifiedNumber(strippedNumberStr)
    }
}

class NumberTooShort: Exception()
class NumberTooLong: Exception()
class UnknownCarrier: Exception()
class InvalidNumberType: Exception()