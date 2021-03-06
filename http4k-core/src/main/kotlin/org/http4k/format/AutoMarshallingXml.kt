package org.http4k.format

import org.http4k.asString
import org.http4k.core.Body
import org.http4k.core.ContentType.Companion.APPLICATION_XML
import org.http4k.lens.BiDiBodyLensSpec
import org.http4k.lens.BiDiWsMessageLensSpec
import org.http4k.lens.ContentNegotiation
import org.http4k.lens.Meta
import org.http4k.lens.ParamMeta.ObjectParam
import org.http4k.lens.httpBodyRoot
import org.http4k.lens.string
import org.http4k.websocket.WsMessage
import kotlin.reflect.KClass

abstract class AutoMarshallingXml {

    inline fun <reified T : Any> String.asA(): T = asA(this)

    @JvmName("stringAsA")
    fun <T : Any> String.asA(target: KClass<T>): T = asA(this, target)

    @JvmName("stringAsA")
    inline fun <reified T : Any> asA(input: String): T = asA(input, T::class)

    abstract fun <T : Any> asA(input: String, target: KClass<T>): T

    abstract fun Any.asXmlString(): String

    @JvmName("anyAsXmlString")
    fun asXmlString(input: Any): String = input.asXmlString()

    inline fun <reified T : Any> Body.Companion.auto(description: String? = null, contentNegotiation: ContentNegotiation = ContentNegotiation.None): BiDiBodyLensSpec<T> =
        httpBodyRoot(listOf(Meta(true, "body", ObjectParam, "body", description)), APPLICATION_XML, contentNegotiation)
            .map({ it.payload.asString() }, { Body(it) })
            .map({ it.asA<T>() }, { it.asXmlString() })

    inline fun <reified T : Any> WsMessage.Companion.auto(): BiDiWsMessageLensSpec<T> = WsMessage.string().map({ it.asA<T>() }, { it.asXmlString() })
}