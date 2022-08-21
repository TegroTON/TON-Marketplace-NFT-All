<template>
  <header class="header">
    <div class="container-fluid">
      <nav class="navbar navbar-expand-lg d-flex align-items-center p-0">
        <a class="navbar-logo me-5" href="/">
          <img class="navbar-logo__img" src="/img/logo/apple-icon-57x57.png"
               alt="Libermall - NFT Marketplace">
          <span class="navbar-logo__name d-none d-md-block">Libermall</span>
        </a>
        <button class="navbar-toggler btn btn-outline-secondary order-2 order-xl-1 ms-3" type="button"
                data-bs-toggle="collapse" data-bs-target="#navbarHeader" aria-controls="navbarSupportedContent"
                aria-expanded="false" aria-label="Toggle navigation">
          <font-awesome-icon icon="fa-solid fa-bars" class="fs-20"></font-awesome-icon>
          <!-- TODO: Should be fa-regular here-->
        </button>
        <div class="collapse navbar-collapse " id="navbarHeader">
          <form
              class="header__search flex-fill d-block d-lg-none d-xl-block mx-0 mx-lg-5 mb-3 mb-lg-0 order-1 order-lg-2">
            <div class="input-group">
              <input type="text" class="form-control" placeholder="Search ..." aria-label="Search"
                     aria-describedby="basic-addon2">
              <div class="input-group-text p-0">
                <button class="btn" type="submit">
                  <font-awesome-icon icon="fa-solid fa-magnifying-glass" class="color-gray"></font-awesome-icon>
                </button>
              </div>
            </div>
          </form>
          <div class="dropdown order-4 order-lg-1">
            <button class="btn btn-primary" type="button" id="dropdownMenuButton1" data-bs-toggle="dropdown"
                    aria-expanded="false">
              <font-awesome-icon icon="fa-solid fa-border-all" class="me-2">
                <!-- TODO: fa-regular fa-grid-2 -->
              </font-awesome-icon>
              Explore
            </button>
            <ul class="dropdown-menu animate slideIn mt-3" aria-labelledby="dropdownMenuButton1">
              <li>
                <a class="dropdown-item" href="#">
                  <font-awesome-icon icon="fa-regular fa-images" class="me-3">
                    <!-- TODO: fa-regular fa-hexagon-vertical-nft-slanted -->
                  </font-awesome-icon>
                  All NFTs
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <font-awesome-icon icon="fa-regular fa-square" class="me-3">
                    <!--TODO: fa-regular fa-hexagon-image -->
                  </font-awesome-icon>
                  Solanas
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <font-awesome-icon icon="fa-solid fa-paintbrush" class="me-3">
                    <!-- TODO: fa-regular fa-paintbrush-fine -->
                  </font-awesome-icon>
                  Art
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <font-awesome-icon icon="fa-solid fa-frog" class="me-3">
                    <!-- TODO: fa-regular fa-rabbit -->
                  </font-awesome-icon>
                  Collectibles
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <font-awesome-icon icon="fa-solid fa-server" class="me-3">
                    <!-- TODO: fa-regular fa-server -->
                  </font-awesome-icon>
                  Domain Names
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <font-awesome-icon icon="fa-solid fa-music" class="me-3">
                    <!-- TODO: fa-regular fa-music -->
                  </font-awesome-icon>
                  Music
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <font-awesome-icon icon="fa-solid fa-camera" class="me-3">
                    <!-- TODO: fa-regular fa-camera -->
                  </font-awesome-icon>
                  Photography
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <font-awesome-icon icon="fa-regular fa-futbol" class="me-3"></font-awesome-icon>
                  Sports
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <font-awesome-icon icon="fa-solid fa-heart-circle-bolt" class="me-3">
                    <!-- TODO: fa-regular fa-heart-circle-bolt -->
                  </font-awesome-icon>
                  Trading Cards
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <font-awesome-icon icon="fa-solid fa-money-bills" class="me-3">
                    <!-- TODO: fa-regular fa-money-bills-simple -->
                  </font-awesome-icon>
                  Utility
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <font-awesome-icon icon="fa-solid fa-vr-cardboard" class="me-3">
                    <!-- TODO: fa-regular fa-vr-cardboard -->
                  </font-awesome-icon>
                  Virtual Worlds
                </a>
              </li>
            </ul>
          </div>
          <ul class="navbar-nav d-flex flex-row py-4 py-lg-0 ms-0 ms-lg-auto pe-5 order-2 order-lg-3">
            <li class="nav-item">
              <a class="nav-link" href="#">Stats</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#">Resources</a>
            </li>
            <li class="nav-item">
              <a class="nav-link" href="#">Create</a>
            </li>
          </ul>
          <a href="#!" class="btn btn-secondary btn-mobile-fixed order-3 order-lg-4">
            <font-awesome-icon icon="fa-solid fa-right-to-bracket" class="me-2">
              <!-- TODO: fa-regular fa-arrow-right-to-arc -->
            </font-awesome-icon>
            Connect
          </a>
        </div>
      </nav>
    </div>
  </header>
</template>
