/**
 * Created by Yasukazu on 2016/12/29.
 * GUI main outside from the package
 * 2016-12-29 BinarySearchTreeFrame (accepts General type)
 */
import jp.yasukazu.kotlin.train.BinarySearchTreeModel
import jp.yasukazu.kotlin.train.BinarySearchTreeFrame
import javax.swing.JFrame
import javax.swing.SwingUtilities

fun main(args:Array<String>){
    val treeModel = BinarySearchTreeModel<Float>()
    args.forEach { a ->
        val i = try { a.toFloat() } catch (e: NumberFormatException){
            println("$a caused $e")
            null }
        if (i != null){
            treeModel += i
        }
    }
    val stringTreeModel = BinarySearchTreeModel<String>()
    args.forEach { i ->
            stringTreeModel += i
    }
    SwingUtilities.invokeLater {
        val intTreeFrame = BinarySearchTreeFrame(treeModel){try{it.toFloat()}catch (e:NumberFormatException){null}}
        with(intTreeFrame){
            title = "Real Number Tree"
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            pack()
            isVisible = true
        }
        val stringTreeFrame = BinarySearchTreeFrame(stringTreeModel){it}
        with(stringTreeFrame){
            title = "String Tree"
            defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            pack()
            isVisible = true
        }
    }
}

