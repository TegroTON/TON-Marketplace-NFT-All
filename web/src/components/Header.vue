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
          <i class="fa-regular fa-bars fs-20"></i>
        </button>
        <div class="collapse navbar-collapse " id="navbarHeader">
          <form
              class="header__search flex-fill d-block d-lg-none d-xl-block mx-0 mx-lg-5 mb-3 mb-lg-0 order-1 order-lg-2">
            <div class="input-group">
              <input type="text" class="form-control" placeholder="Search ..." aria-label="Search"
                     aria-describedby="basic-addon2">
              <div class="input-group-text p-0">
                <button class="btn" type="submit">
                  <i class="fa-solid fa-magnifying-glass color-grey"></i>
                </button>
              </div>
            </div>
          </form>
          <div class="dropdown order-4 order-lg-1">
            <button class="btn btn-primary" type="button" id="dropdownMenuButton1" data-bs-toggle="dropdown"
                    aria-expanded="false">
              <i class="fa-regular fa-grid-2 me-2"></i>
              Explore
            </button>
            <ul class="dropdown-menu animate slideIn mt-3" aria-labelledby="dropdownMenuButton1">
              <li>
                <a class="dropdown-item" href="#">
                  <i class="fa-regular fa-hexagon-vertical-nft-slanted me-3"></i>
                  All NFTs
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <i class="fa-regular fa-hexagon-image me-3"></i>
                  Solanas
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <i class="fa-regular fa-paintbrush-fine me-3"></i>
                  Art
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <i class="fa-regular fa-rabbit me-3"></i>
                  Collectibles
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <i class="fa-regular fa-server me-3"></i>
                  Domain Names
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <i class="fa-regular fa-music me-3"></i>
                  Music
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <i class="fa-regular fa-camera me-3"></i>
                  Photography
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <i class="fa-regular fa-futbol me-3"></i>
                  Sports
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <i class="fa-regular fa-heart-circle-bolt me-3"></i>
                  Trading Cards
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <i class="fa-regular fa-money-bills-simple me-3"></i>
                  Utility
                </a>
              </li>
              <li>
                <a class="dropdown-item" href="#">
                  <i class="fa-regular fa-vr-cardboard me-3"></i>
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
            <i class="fa-regular fa-arrow-right-to-arc me-2"></i>
            Connect
          </a>
        </div>
      </nav>
    </div>
  </header>
</template>
