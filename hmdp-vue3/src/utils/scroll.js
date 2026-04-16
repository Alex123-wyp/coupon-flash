// Throttle function
export const throttle = (fn, delay) => {
  let timer = null
  return function (...args) {
    if (timer) return
    timer = setTimeout(() => {
      fn.apply(this, args)
      timer = null
    }, delay)
  }
}

// Get the scroll position
export const getScrollTop = () => {
  return document.documentElement.scrollTop || document.body.scrollTop
}

// Set the scroll position
export const setScrollTop = (value) => {
  document.documentElement.scrollTop = value
  document.body.scrollTop = value
}
