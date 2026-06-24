---
layout: default
title: Just a website
home: true
---

<section class="landing">
  <div class="landing__avatar">
    <img src="{{ "/assets/profile.png" | relative_url }}" alt="Thang Nguyen">
  </div>

  <h1>Hi there!</h1>
  <p>I'm <strong>Thang Nguyen</strong>, a Senior Backend Engineer currently at Garner Health.</p>
  <p>I was previously at a mental health startup, The New York Times & Zipcar.</p>

  <p>
    Some of my interests include:
    <button type="button" class="text-button" data-modal-target="bjj-modal">BJJ</button>,
    <button type="button" class="text-button" data-modal-target="photog-modal">Photography</button>,
    and
    <button type="button" class="text-button" data-modal-target="bread-modal">baking</button>.
  </p>

  <p>You can contact me by email: First Name @ this domain</p>

  <div class="landing__social" aria-label="Social links">
    <a href="https://github.com/nguyent" aria-label="GitHub">
      <svg aria-hidden="true" viewBox="0 0 24 24">
        <path d="M12 .5A12 12 0 0 0 8.2 23.9c.6.1.8-.3.8-.6v-2.1c-3.3.7-4-1.4-4-1.4-.5-1.3-1.2-1.7-1.2-1.7-1-.7.1-.7.1-.7 1.1.1 1.7 1.2 1.7 1.2 1 1.7 2.6 1.2 3.3.9.1-.7.4-1.2.7-1.5-2.6-.3-5.4-1.3-5.4-5.9 0-1.3.5-2.4 1.2-3.2-.1-.3-.5-1.5.1-3.2 0 0 1-.3 3.3 1.2a11.3 11.3 0 0 1 6 0c2.3-1.5 3.3-1.2 3.3-1.2.6 1.7.2 2.9.1 3.2.8.8 1.2 1.9 1.2 3.2 0 4.6-2.8 5.6-5.4 5.9.4.4.8 1.1.8 2.2v3.1c0 .3.2.7.8.6A12 12 0 0 0 12 .5z"/>
      </svg>
    </a>
    <a href="https://linkedin.com/in/iamthang" aria-label="LinkedIn">
      <svg aria-hidden="true" viewBox="0 0 24 24">
        <path d="M22.2 0H1.8C.8 0 0 .8 0 1.7v20.6C0 23.2.8 24 1.8 24h20.4c1 0 1.8-.8 1.8-1.7V1.7C24 .8 23.2 0 22.2 0zM7.1 20.5H3.6V9h3.5v11.5zM5.3 7.4a2.1 2.1 0 1 1 0-4.2 2.1 2.1 0 0 1 0 4.2zm15.1 13.1h-3.5v-5.6c0-1.3 0-3-1.9-3s-2.1 1.4-2.1 2.9v5.7H9.4V9h3.4v1.6h.1c.5-.9 1.6-1.9 3.4-1.9 3.6 0 4.2 2.4 4.2 5.5v6.3z"/>
      </svg>
    </a>
    <a href="{{ "/Nguyen.Thang-Resume.pdf" | relative_url }}" target="_blank" rel="noopener">
      <img src="{{ "/assets/resume.png" | relative_url }}" alt="Resume">
    </a>
  </div>
</section>

<div class="image-modal" id="bjj-modal" aria-hidden="true">
  <button class="image-modal__backdrop" type="button" data-modal-close aria-label="Close BJJ photo"></button>
  <div class="image-modal__dialog" role="dialog" aria-modal="true" aria-label="BJJ photo">
    <button class="image-modal__close" type="button" data-modal-close aria-label="Close">Close</button>
    <img src="{{ "/assets/landing-bjj.jpg" | relative_url }}" alt="BJJ">
  </div>
</div>

<div class="image-modal" id="bread-modal" aria-hidden="true">
  <button class="image-modal__backdrop" type="button" data-modal-close aria-label="Close baking photo"></button>
  <div class="image-modal__dialog" role="dialog" aria-modal="true" aria-label="Baking photo">
    <button class="image-modal__close" type="button" data-modal-close aria-label="Close">Close</button>
    <img src="{{ "/assets/landing-bake.jpg" | relative_url }}" alt="Baking">
  </div>
</div>

<div class="image-modal image-modal--wide" id="photog-modal" aria-hidden="true">
  <button class="image-modal__backdrop" type="button" data-modal-close aria-label="Close photography photo"></button>
  <div class="image-modal__dialog" role="dialog" aria-modal="true" aria-label="Photography photo">
    <button class="image-modal__close" type="button" data-modal-close aria-label="Close">Close</button>
    <img src="{{ "/assets/landing-photog.jpg" | relative_url }}" alt="Photography">
  </div>
</div>

<script>
  (function () {
    var openButtons = document.querySelectorAll("[data-modal-target]");
    var closeButtons = document.querySelectorAll("[data-modal-close]");

    function closeModal(modal) {
      modal.classList.remove("image-modal--open");
      modal.setAttribute("aria-hidden", "true");
    }

    openButtons.forEach(function (button) {
      button.addEventListener("click", function () {
        var modal = document.getElementById(button.getAttribute("data-modal-target"));
        if (!modal) {
          return;
        }

        modal.classList.add("image-modal--open");
        modal.setAttribute("aria-hidden", "false");
      });
    });

    closeButtons.forEach(function (button) {
      button.addEventListener("click", function () {
        closeModal(button.closest(".image-modal"));
      });
    });

    document.addEventListener("keydown", function (event) {
      if (event.key !== "Escape") {
        return;
      }

      document.querySelectorAll(".image-modal--open").forEach(closeModal);
    });
  }());
</script>
