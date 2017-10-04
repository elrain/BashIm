package com.elrain.bashim.utils.parser

import com.elrain.bashim.dao.BashItem
import org.xml.sax.InputSource
import java.io.InputStream
import java.io.InputStreamReader
import javax.xml.parsers.SAXParserFactory

class XmlParser(private val stream: InputStream) : Parser {

    private val ENCODING = "windows-1251"

    override fun parse(): List<BashItem> {
        val itemsList: MutableList<BashItem> = mutableListOf()
        val saxParserFactory = SAXParserFactory.newInstance()
        val saxParser = saxParserFactory.newSAXParser()
        saxParser.parse(prepareInputStream(stream), ParserHandler(itemsList))
        return itemsList
    }

    private fun prepareInputStream(stream: InputStream): InputSource {
        val r = InputStreamReader(stream, ENCODING)
        val inputSource = InputSource(r)
        inputSource.encoding = ENCODING
        return inputSource
    }
}