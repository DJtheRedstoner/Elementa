package club.sk1er.elementa.constraints

class PixelConstraint(private val value: Float) : Constraint() {
    override fun getValue() = value
}