package com.remcoil.module.comment

import com.remcoil.data.model.comment.Comment
import com.remcoil.service.comment.CommentService
import com.remcoil.utils.safetyReceive
import com.remcoil.utils.safetyRespond
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.instance
import org.kodein.di.ktor.closestDI

fun Application.commentModule() {
    val commentService: CommentService by closestDI().instance()

    routing {
        route("/comment") {
            get {
                val comments = commentService.getAllComments()
                call.respond(comments)
            }
            get("/{action_id}") {
                val comment = call.parameters["action_id"]?.let {actionId ->
                    actionId.toLongOrNull()?.let {
                        commentService.getComment(actionId.toLong())
                    }
                }
                call.safetyRespond(comment, HttpStatusCode.NoContent)
            }
            authenticate("jwt-access") {
                post {
                    call.safetyReceive<Comment> { comment ->
//                        val principal = call.principal<JWTPrincipal>()
                        call.safetyRespond(commentService.createComment(comment), HttpStatusCode.BadRequest)
                    }
                }
                put {
                    call.safetyReceive<Comment> { comment ->
                        commentService.updateComment(comment)
                        call.respond(HttpStatusCode.OK)
                    }
                }
                delete("/{action_id}") {
                    commentService.deleteComment(call.parameters["action_id"]!!.toLong())
                    call.respond(HttpStatusCode.OK)
                }
            }
        }
    }
}