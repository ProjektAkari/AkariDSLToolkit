package pw.akari.dsltk.parse

sealed interface Node

sealed interface ExprNode : Node

class BlockNode(val actions: List<ActionNode>) : Node

class ActionNode(val action: String, val arguments: Map<String, ExprNode>) : Node

sealed class ConstantNode<T>(val value: T) : ExprNode

class NumberNode(value: Double) : ConstantNode<Double>(value)

class StringNode(value: String) : ConstantNode<String>(value)

class BooleanNode private constructor(value: Boolean) : ConstantNode<Boolean>(value) {
    companion object {
        @JvmStatic val TRUE = BooleanNode(true)
        @JvmStatic val FALSE = BooleanNode(false)
    }
}

class ExprActionNode(val elements: List<ExprNode>) : ExprNode

class VarAccessNode(val varName: String) : ExprNode



object ASTPrettyPrinter {
    fun print(node: Node) {
        when(node) {
            is ActionNode -> print(node)
            is BlockNode -> print(node)
            is BooleanNode -> print(node)
            is NumberNode -> print(node)
            is StringNode -> print(node)
            is VarAccessNode -> print(node)
            else -> TODO()
        }
    }

    fun print(node: BlockNode) {
        for (action in node.actions) {
            print(action)
        }
    }

    fun print(node: ActionNode) {
        println("Action { action = ${node.action}, arguments = {")
        for ((name, value) in node.arguments) {
            print("  $name: ")
            print(value)
        }
        println("}")
    }

    fun print(node: BooleanNode) {
        println("Constant(${node.value})")
    }

    fun print(node: NumberNode) {
        println("Constant(${node.value})")
    }

    fun print(node: StringNode) {
        println("Constant(${node.value})")
    }

    fun print(node: VarAccessNode) {
        println("VarAccess(${node.varName})")
    }

}
