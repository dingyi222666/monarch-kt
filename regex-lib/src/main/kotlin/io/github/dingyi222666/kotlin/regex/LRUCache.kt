/*
 * monarch-kt - Kotlin port of Monarch library.
 * https://github.com/dingyi222666/monarch-kt
 * Copyright (C) 2024 dingyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.github.dingyi222666.kotlin.regex

import java.util.*

class LRUCache<K, V>(private val capacity: Int) {

    private inner class Node(val key: K, var value: V) {
        var prev: Node? = null
        var next: Node? = null
    }

    private val cache = LinkedHashMap<K, Node>(capacity, 0.75f, true)

    private var head: Node? = null
    private var tail: Node? = null

    fun get(key: K): V? {
        val node = cache[key] ?: return null
        moveToHead(node)
        return node.value
    }

    fun put(key: K, value: V) {
        val newNode = Node(key, value)
        val existingNode = cache[key]

        if (existingNode != null) {
            existingNode.value = value
            moveToHead(existingNode)
        } else {
            cache[key] = newNode

            if (cache.size > capacity) {
                evictLast()
            } else {
                addToHead(newNode)
            }
        }
    }

    private fun addToHead(node: Node) {
        node.prev = null
        node.next = head

        val head = head
        if (head != null) {
            head.prev = node
        }

        this.head = node

        if (tail == null) {
            tail = node
        }
    }

    private fun moveToHead(node: Node) {
        if (node === head) {
            return
        }

        if (node === tail) {
            tail = node.prev
        }

        remove(node)
        addToHead(node)
    }

    private fun remove(node: Node) {
        val prev = node.prev
        if (prev != null) {
            prev.next = node.next
        } else {
            head = node.next
        }

        val next = node.next
        if (next != null) {
            next.prev = node.prev
        } else {
            tail = node.prev
        }
    }

    private fun evictLast() {
        val lastNode = tail ?: throw IllegalStateException("LRUCache is empty")
        cache.remove(lastNode.key)
        remove(lastNode)
    }
}
