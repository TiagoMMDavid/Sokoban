package isel.poo.li22d.g6.lib;

/**
 * Interface to implemented by listeners of animations.
 * @author Palex
 */
public interface OnFinishAnimationListener {

	/**
	 * When animations are finished
     * @see Animator#triggerOnFinishAnimations
	 * @param tag Tag provided in triggerOnFinishAnimations()
	 */
	public void onFinish(Object tag);
}