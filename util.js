const output_attr = "data-output";
const selected_attr = "data-selected";
const button_class = "sc-output-button";
const output_class = "sc-output";
window.addEventListener("DOMContentLoaded", (e_) => {
  {
    let target = null;
    for (let t of document.getElementsByClassName(output_class)) {
      const output = t.getAttribute(output_attr);
      if (target === null || output == target) {
        t.style.display = "";
        target = output;
      }
    }
    for (let l2 of document.getElementsByClassName(button_class)) {
      l2.setAttribute(
        selected_attr,
        l2.getAttribute(output_attr) == target ? "true" : ""
      );
    }
  }
  for (let l of document.getElementsByClassName(button_class))
    (() => {
      const target = l.getAttribute(output_attr);
      l.addEventListener("click", (e_) => {
        for (let l2 of document.getElementsByClassName(button_class)) {
          l2.setAttribute(
            selected_attr,
            l2.getAttribute(output_attr) == target ? "true" : ""
          );
        }
        for (let t of document.getElementsByClassName(output_class)) {
          if (t.getAttribute(output_attr) == target) {
            t.style.display = "";
          } else {
            t.style.display = "none";
          }
        }
      });
    })();
});
