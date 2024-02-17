/*
 * monarch-kt - Kotlin port of Monarch library.
 * https://github.com/dingyi222666/monarch-kt
 * Copyright (C) 2024-2024  dingyi
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

package io.github.dingyi222666.monarch.extension

@JvmInline
value class UnionType<A, B>(val value: Any)

inline val <reified A> UnionType<A, *>.isLeft: Boolean
    get() = value is A

inline val <reified B> UnionType<*, B>.isRight: Boolean
    get() = value is B

inline val <reified B> UnionType<*, B>.right: B
    get() = value as B

inline val <reified B> UnionType<*, B>.rightOrNull: B?
    get() = value as? B

inline val <reified A> UnionType<A, *>.left: A
    get() = value as A

inline val <reified A> UnionType<A, *>.leftOrNull: A?
    get() = value as? A

