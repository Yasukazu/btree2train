package jp.yasukazu.kotlin.train.tree

/**
 * Created by Yasukazu on 2017/01/23.
 */
enum class InsertedPos { LEFT, NEW, RIGHT, NONE }
enum class DeleteResult {SELF_DELETE, LEFT_REPLACE, RIGHT_REPLACE, PREDEC_REPLACE, NO_MATCH, EMPTY}
class IllegalAssignmentException(msg: String) : Exception(msg)
class _BreakException(val pos: InsertedPos) : Exception()
open class InsertException: InsertDeleteException()
class InsertFailException: InsertException()
open class InsertDeleteException(msg: String?=null): Exception(msg)
open class DeleteException(msg: String?=null): InsertDeleteException(msg)
class DeleteFailException(msg: String?=null): DeleteException(msg)
class DeleteSuccessException(val result: DeleteResult): DeleteException()
class _FoundException(val found: Boolean) : Exception()//, val node: SearchBinaryNodeInterface<T>?, val parent: SearchBinaryNodeInterface<T>?): Exception()
