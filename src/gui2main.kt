/**
 * Created by Yasukazu on 2016/12/29.
 * GUI main outside from the package
 */
import jp.yasukazu.kotlin.train.BinarySearchTreeModel
import jp.yasukazu.kotlin.train.IntBinarySearchTreeFrame
import javax.swing.JFrame
import javax.swing.SwingUtilities

fun main(args:Array<String>){
    val treeModel = BinarySearchTreeModel<Int>()
    args.forEach { a ->
        val i = try { a.toInt() } catch (e: NumberFormatException){
            println("$a caused $e")
            null }
        if (i != null){
            treeModel += i
        }
    }
    SwingUtilities.invokeLater {
        val intTreeFrame = IntBinarySearchTreeFrame(treeModel)
        with(intTreeFrame){
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            pack()
            isVisible = true
        }
    }
}

