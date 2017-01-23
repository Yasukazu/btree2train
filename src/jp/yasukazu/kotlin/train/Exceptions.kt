package jp.yasukazu.kotlin.train

/**
 * Created by Yasukazu on 2017/01/23.
 */
enum class InsertedPos { LEFT, NEW, RIGHT, NONE }
enum class DeleteResult {SELF_DELETE, LEFT_REPLACE, RIGHT_REPLACE, PREDEC_REPLACE, NO_MATCH, EMPTY}
class _BreakException(val pos: InsertedPos) : Exception()
open class InsertException: InsertDeleteException()
class InsertFailException: InsertException()
open class InsertDeleteException: Exception()
open class DeleteException: InsertDeleteException()
class DeleteFailException: DeleteException()
class DeleteSuccessException(val result: DeleteResult): DeleteException()
class _FoundException(val found: Boolean) : Exception()//, val node: SearchBinaryNodeInterface<T>?, val parent: SearchBinaryNodeInterface<T>?): Exception()
