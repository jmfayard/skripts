#!/usr/bin/env kotlin-script.sh
package expose

import com.squareup.moshi.Moshi
import okio.buffer
import okio.sink
import okio.source
import org.docopt.Docopt
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SqlLogger
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.statements.StatementContext
import org.jetbrains.exposed.sql.statements.expandArgs
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import java.io.File
import java.util.UUID

private val HELP =
    """Generate SQL.

Usage:
  expose.py accounts <users.json>

Options:
  -h --help     Show this screen.
  --version     Show version.
"""
// See http://try.docopt.org/ https://github.com/docopt/docopt.java

fun main(args: Array<String>) {
    val docopt = Docopt(HELP).withVersion("0.1").parse(args.toList())
    val file = File(docopt.get("<users.json>") as String)
    val moshi = Moshi.Builder().build()
    val accounts = moshi.adapter(Accounts::class.java).fromJson(file.source().buffer())!!
    println(accounts)
    testSql(accounts.userAccounts, file.resolveSibling("oldusers.sql"))
}

private data class Account(
    val accountId: String = "",
    val accountName: String = "",
    val role: String = "",
    val firstName: String = "",
    val middleNames: String = "",
    val lastName: String = "",
    val suffix: String = "",
    val preferredName: String = "",
    val gender: String = "",
    val entityName: String = ""
)

private data class Accounts(
    val userAccounts: List<Account>
)

object MtapAccount : Table("mtap_account") {
    val date_time = datetime("date_time")
    val account_id = uuid("account_id").primaryKey()
}

object MtapKyc : Table("mtap_kyc") {
    val date_time = datetime("date_time")
    val account_id = uuid("account_id").references(MtapAccount.account_id)
    val account_name = varchar("account_name", 64)
    val active = bool("active")
    val active_card = bool("active_card")
    val role = this.enumerationByName("role", 32, enum_role::class.java)
    val first_name = varchar("first_name", 64)
    val middle_names = varchar("middle_names", 64)
    val last_name = varchar("last_name", 64)
    val suffix = varchar("suffix", 32)
    val nick_name = varchar("nick_name", 32)
    val entity_name = varchar("entity_name", 32)
}

object FudgeboxNode : Table("fudgebox_node") {
    val hash = binary("hash", 64).primaryKey()
}

object FudgeboxCounter : Table("fudgebox_counter") {
    val account_id = uuid("account_id").references(MtapAccount.account_id).primaryKey()
    val offline_counter = integer("offline_counter")
    val remote_counter = integer("remote_counter")
}

object FudgeboxHead : Table("fudgebox_head") {
    val account_id = uuid("account_id").references(MtapAccount.account_id).primaryKey()
    val node = binary("node", 128).references(FudgeboxNode.hash).nullable()
    val currency = varchar("currency", 3)
    val balance = integer("balance")
}

enum class enum_role {
    messager, beneficiary, vendor, agent, donor, mint
}

private fun testSql(userAccounts: List<Account>, outputSql: File) {
//    Database.connect("jdbc:h2:mem:test", driver = "org.h2.Driver")
    val output = outputSql.sink().buffer()
    Database.connect("jdbc:postgresql://localhost:5432/mtap", driver = "org.postgresql.Driver", user = "jmfayard")

    transaction {
        logger.addLogger(StdOutSqlLogger)
        logger.addLogger(object : SqlLogger {
            override fun log(context: StatementContext, transaction: Transaction) {
                output.writeUtf8("${context.expandArgs(transaction)};\n")
            }
        })

//        drop(MtapKyc, MtapAccount, FudgeboxCounter, FudgeboxHead, FudgeboxNode)
//        create(MtapKyc, MtapAccount, FudgeboxCounter, FudgeboxHead, FudgeboxNode)

        for (account in userAccounts) {
            MtapAccount.insert {
                it[account_id] = UUID.fromString(account.accountId)
                it[date_time] = DateTime.now()
            }
        }
        for (account in userAccounts) {
            MtapKyc.insert {
                it[date_time] = DateTime.now()
                it[account_id] = UUID.fromString(account.accountId)
                it[account_name] = account.accountName
                it[active] = true
                it[active_card] = true
                it[role] = enum_role.beneficiary
                it[first_name] = account.firstName
                it[last_name] = account.lastName
                it[middle_names] = account.middleNames
                it[suffix] = ""
                it[nick_name] = ""
                it[entity_name] = ""
            }
        }
        for (account in userAccounts) {
            FudgeboxHead.insert {
                it[account_id] = UUID.fromString(account.accountId)
                it[node] = null
                it[currency] = "USD"
                it[balance] = 0
            }
        }
        for (account in userAccounts) {
            FudgeboxCounter.insert {
                it[account_id] = UUID.fromString(account.accountId)
                it[offline_counter] = 0
                it[remote_counter] = 0
            }
        }
    }
    output.close()
    println("Written to ${outputSql.absolutePath}")
}
