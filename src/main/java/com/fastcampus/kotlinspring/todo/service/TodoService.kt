package com.fastcampus.kotlinspring.todo.service

import com.fastcampus.kotlinspring.todo.api.model.TodoRequest
import com.fastcampus.kotlinspring.todo.api.model.TodoResponse
import com.fastcampus.kotlinspring.todo.domain.Todo
import com.fastcampus.kotlinspring.todo.domain.TodoRepository
import org.springframework.data.domain.Sort
import org.springframework.data.domain.Sort.*
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.LocalDateTime

@Service
class TodoService(
    private val todoRepository: TodoRepository,
) {

    @Transactional(readOnly = true)
    fun findAll() : List<Todo> =
        todoRepository.findAll(by(Direction.DESC, "id"))

    @Transactional(readOnly = true)
    fun findById(id: Long) : Todo =
        todoRepository.findByIdOrNull(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND)

    @Transactional
    fun create(request: TodoRequest?): Todo {
        // request.title -> nullable 로 받았기 때문에 안전연산자 ? 를 사용하지 않으면 오류
        checkNotNull(request) { "TodoRequest is null" }
        request.title // checkNotNull 메서드로 인해 null인 경우 exceiption 을 발생하고 null이
        // 아닌 경우 그대로 value 를 반환하기 때문에 안전연산자를 사용하지 않아도 무방

        val todo = Todo (
            title = request.title,
            description = request.description,
            done = request.done,
            createdAt = LocalDateTime.now(),
        )
        return todoRepository.save(todo)
    }

    @Transactional
    fun update(id: Long, request: TodoRequest?) : Todo {
        checkNotNull(request) {"TodoRequest is null"}

        return findById(id).let {
            it.update(request.title, request.description, request.done)
            todoRepository.save(it)
        }
    }

    fun delete(id: Long) = todoRepository.deleteById(id)
}