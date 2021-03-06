package club.sk1er.elementa.effects

import club.sk1er.elementa.components.UIBlock
import club.sk1er.elementa.state.BasicState
import club.sk1er.elementa.state.State
import club.sk1er.elementa.utils.guiHint
import java.awt.Color

/**
 * Draws a basic, rectangular outline of the specified [color] and [width] around
 * this component. The outline will be drawn before this component's children are drawn,
 * so all children will render above the outline.
 */
class OutlineEffect @JvmOverloads constructor(
    color: Color,
    width: Float,
    var drawAfterChildren: Boolean = false,
    var drawInsideChildren: Boolean = false,
    sides: Set<Side> = setOf(Side.Left, Side.Top, Side.Right, Side.Bottom)
) : Effect() {
    private var hasLeft = Side.Left in sides
    private var hasTop = Side.Top in sides
    private var hasRight = Side.Right in sides
    private var hasBottom = Side.Bottom in sides

    private var colorState: State<Color> = BasicState(color)
    private var widthState: State<Float> = BasicState(width)

    var color: Color
        get() = colorState.get()
        set(value) {
            colorState.set(value)
        }

    var width: Float
        get() = widthState.get()
        set(value) {
            widthState.set(value)
        }

    fun bindColor(state: State<Color>) = apply {
        colorState = state
    }

    fun bindWidth(state: State<Float>) = apply {
        widthState = state
    }

    var sides = sides
        set(value) {
            field = value
            hasLeft = Side.Left in sides
            hasTop = Side.Top in sides
            hasRight = Side.Right in sides
            hasBottom = Side.Bottom in sides
        }

    fun addSide(side: Side) = apply {
        sides = sides + side
    }

    fun removeSide(side: Side) = apply {
        sides = sides - side
    }

    override fun beforeChildrenDraw() {
        if (!drawAfterChildren)
            drawOutline()
    }

    override fun afterDraw() {
        if (drawAfterChildren)
            drawOutline()
    }

    private fun drawOutline() {
        val color = colorState.get()
        val width = widthState.get()

        val left = boundComponent.getLeft().toDouble()
        val right = boundComponent.getRight().toDouble()
        val top = boundComponent.getTop().toDouble()
        val bottom = boundComponent.getBottom().toDouble()

        val leftHinted = left.guiHint(true)
        val rightHinted = right.guiHint(false)
        val topHinted = top.guiHint(true)
        val bottomHinted = bottom.guiHint(false)

        val leftBounds = if (drawInsideChildren) {
            leftHinted to (left + width).guiHint(true)
        } else (left - width).guiHint(true) to leftHinted

        val topBounds = if (drawInsideChildren) {
            topHinted to (top + width).guiHint(true)
        } else (top - width).guiHint(true) to topHinted

        val rightBounds = if (drawInsideChildren) {
            (right - width).guiHint(false) to rightHinted
        } else rightHinted to (right + width).guiHint(false)

        val bottomBounds = if (drawInsideChildren) {
            (bottom - width).guiHint(false) to bottomHinted
        } else bottomHinted to (bottom + width).guiHint(false)

        // Left outline block
        if (hasLeft)
            UIBlock.drawBlock(color, leftBounds.first, topHinted, leftBounds.second, bottomHinted)

        // Top outline block
        if (hasTop)
            UIBlock.drawBlock(color, leftHinted, topBounds.first, rightHinted, topBounds.second)

        // Right outline block
        if (hasRight)
            UIBlock.drawBlock(color, rightBounds.first, topHinted, rightBounds.second, bottomHinted)

        // Bottom outline block
        if (hasBottom)
            UIBlock.drawBlock(color, leftHinted, bottomBounds.first, rightHinted, bottomBounds.second)

        if (!drawInsideChildren) {
            // Top left square
            if (hasLeft && hasTop)
                UIBlock.drawBlock(color, leftBounds.first, topBounds.first, leftHinted, topHinted)

            // Top right square
            if (hasRight && hasTop)
                UIBlock.drawBlock(color, rightHinted, topBounds.first, rightBounds.second, topHinted)

            // Bottom right square
            if (hasRight && hasBottom)
                UIBlock.drawBlock(color, rightHinted, bottomHinted, rightBounds.second, bottomBounds.second)

            // Bottom left square
            if (hasBottom && hasLeft)
                UIBlock.drawBlock(color, leftBounds.first, bottomHinted, leftHinted, bottomBounds.second)
        }
    }

    enum class Side {
        Left,
        Top,
        Right,
        Bottom,
    }
}
