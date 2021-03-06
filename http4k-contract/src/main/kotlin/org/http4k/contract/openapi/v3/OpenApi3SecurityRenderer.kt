package org.http4k.contract.openapi.v3

import org.http4k.contract.openapi.Render
import org.http4k.contract.openapi.SecurityRenderer
import org.http4k.contract.security.ApiKeySecurity
import org.http4k.contract.security.BasicAuthSecurity
import org.http4k.contract.security.BearerAuthSecurity
import org.http4k.contract.security.NoSecurity
import org.http4k.contract.security.OAuthSecurity
import org.http4k.contract.security.Security

object OpenApi3SecurityRenderer : SecurityRenderer {
    override fun <NODE> full(security: Security): Render<NODE>? =
        when (security) {
            is BasicAuthSecurity -> {
                {
                    obj(security.name to obj(
                        "scheme" to string("basic"),
                        "type" to string("http")
                    ))
                }
            }
            is BearerAuthSecurity -> {
                {
                    obj(security.name to obj(
                        "scheme" to string("bearer"),
                        "type" to string("http")
                    ))
                }
            }
            is ApiKeySecurity<*> -> {
                {
                    obj(security.name to obj(
                        "type" to string("apiKey"),
                        "in" to string(security.param.meta.location),
                        "name" to string(security.param.meta.name)
                    ))
                }
            }
            is OAuthSecurity -> {
                {
                    obj(security.name to obj(
                        "type" to string("oauth2"),
                        "flows" to obj("authorizationCode" to obj(
                            "authorizationUrl" to string(security.authorizationUrl.toString()),
                            "tokenUrl" to string(security.tokenUrl.toString()),
                            "scopes" to obj(security.scopes.map { it.name to string(it.description) })
                        ))
                    ))
                }
            }

            is NoSecurity -> {
                {
                    nullNode()
                }
            }
            else -> null
        }

    override fun <NODE> ref(security: Security): Render<NODE>? =
        when (security) {
            is ApiKeySecurity<*> -> {
                { obj(security.name to array(emptyList())) }
            }
            is BasicAuthSecurity -> {
                { obj(security.name to array(emptyList())) }
            }
            is BearerAuthSecurity -> {
                { obj(security.name to array(emptyList())) }
            }
            is OAuthSecurity -> {
                { obj(security.name to array(security.scopes.map { string(it.name) })) }
            }
            else -> null
        }
}