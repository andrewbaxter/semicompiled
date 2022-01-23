const output_attr = "data-output";
const selected_attr = "data-selected";
const button_class = "sc-output-button";
const output_class = "sc-output";

const update_target = (target) => {
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
};

window.addEventListener("DOMContentLoaded", (e_) => {
  {
    let target = decodeURI((document.location.hash || "#").substr(1));
    if (target.length == 0)
      for (let t of document.getElementsByClassName(output_class)) {
        target = t.getAttribute(output_attr);
        break;
      }
    update_target(target);
  }
  for (let l of document.getElementsByClassName(button_class)) {
    const target = l.getAttribute(output_attr);
    l.addEventListener("click", (e_) => {
      update_target(target);
    });
  }
});
