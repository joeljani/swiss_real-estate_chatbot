/**
 * Creates a component based on an existing html element and binds it to an event listener
 * @param id of html element
 * @param eventListenerType
 * @param eventListener
 * @param options of the eventlistener
 * @returns {HTMLElement}
 */
const component = (id, eventListenerType, eventListener, options) => {
    const component = document.getElementById(id);
    if(eventListenerType && eventListener) {
        if(options) component.addEventListener(eventListenerType, eventListener, options);
        else component.addEventListener(eventListenerType, eventListener);
    }
    return component;
};

export {component}