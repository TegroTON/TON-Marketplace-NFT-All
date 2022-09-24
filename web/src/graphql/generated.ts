import gql from 'graphql-tag';
import * as Urql from '@urql/vue';
export type Maybe<T> = T | null;
export type InputMaybe<T> = Maybe<T>;
export type Exact<T extends { [key: string]: unknown }> = { [K in keyof T]: T[K] };
export type MakeOptional<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]?: Maybe<T[SubKey]> };
export type MakeMaybe<T, K extends keyof T> = Omit<T, K> & { [SubKey in K]: Maybe<T[SubKey]> };
export type Omit<T, K extends keyof T> = Pick<T, Exclude<keyof T, K>>;
/** All built-in and custom scalars, mapped to their actual values */
export type Scalars = {
  ID: string;
  String: string;
  Boolean: boolean;
  Int: number;
  Float: number;
};

export type Collection = {
  __typename?: 'Collection';
  address: Scalars['ID'];
  coverImage?: Maybe<Scalars['String']>;
  description?: Maybe<Scalars['String']>;
  image?: Maybe<Scalars['String']>;
  itemNumber?: Maybe<Scalars['String']>;
  items: Array<Item>;
  name?: Maybe<Scalars['String']>;
  owner?: Maybe<Profile>;
  ownerNumber: Scalars['String'];
};


export type CollectionItemsArgs = {
  drop?: InputMaybe<Scalars['Int']>;
  take?: InputMaybe<Scalars['Int']>;
};

export type Item = {
  __typename?: 'Item';
  address: Scalars['ID'];
  attributes: Array<ItemAttribute>;
  collection?: Maybe<Collection>;
  description?: Maybe<Scalars['String']>;
  image?: Maybe<Scalars['String']>;
  index?: Maybe<Scalars['String']>;
  name?: Maybe<Scalars['String']>;
  owner?: Maybe<Profile>;
  royalty?: Maybe<Royalty>;
  sale?: Maybe<Sale>;
};

export type ItemAttribute = {
  __typename?: 'ItemAttribute';
  trait: Scalars['String'];
  value: Scalars['String'];
};

export type Profile = {
  __typename?: 'Profile';
  address: Scalars['ID'];
  collections: Array<Collection>;
  ownedItems: Array<Item>;
};


export type ProfileCollectionsArgs = {
  drop?: InputMaybe<Scalars['Int']>;
  take?: InputMaybe<Scalars['Int']>;
};


export type ProfileOwnedItemsArgs = {
  drop?: InputMaybe<Scalars['Int']>;
  take?: InputMaybe<Scalars['Int']>;
};

export type Query = {
  __typename?: 'Query';
  collection: Collection;
  collections: Array<Collection>;
  item: Item;
  profile: Profile;
  transfer: TransactionRequest;
};


export type QueryCollectionArgs = {
  address: Scalars['String'];
};


export type QueryCollectionsArgs = {
  drop?: InputMaybe<Scalars['Int']>;
  take?: InputMaybe<Scalars['Int']>;
};


export type QueryItemArgs = {
  address: Scalars['String'];
};


export type QueryProfileArgs = {
  address: Scalars['String'];
};


export type QueryTransferArgs = {
  destination: Scalars['String'];
  item: Scalars['String'];
  response: Scalars['String'];
};

export type Royalty = {
  __typename?: 'Royalty';
  destination: Scalars['String'];
  value: Scalars['Float'];
};

export type Sale = {
  __typename?: 'Sale';
  address: Scalars['ID'];
  buyPrice?: Maybe<Scalars['String']>;
  fullPrice?: Maybe<Scalars['String']>;
  marketplaceFee?: Maybe<Scalars['String']>;
  networkFee: Scalars['String'];
  royaltyAmount?: Maybe<Scalars['String']>;
};

export type TransactionRequest = {
  __typename?: 'TransactionRequest';
  dest: Scalars['String'];
  payload?: Maybe<Scalars['String']>;
  stateInit?: Maybe<Scalars['String']>;
  value: Scalars['String'];
};

export type CollectionInfoQueryVariables = Exact<{
  address: Scalars['String'];
}>;


export type CollectionInfoQuery = { __typename?: 'Query', collection: { __typename?: 'Collection', address: string, image?: string | null, name?: string | null, description?: string | null, owner?: { __typename?: 'Profile', address: string } | null } };

export type CollectionItemsQueryVariables = Exact<{
  address: Scalars['String'];
  drop?: InputMaybe<Scalars['Int']>;
  take?: InputMaybe<Scalars['Int']>;
}>;


export type CollectionItemsQuery = { __typename?: 'Query', collection: { __typename?: 'Collection', address: string, items: Array<{ __typename?: 'Item', address: string }> } };

export type CollectionStatsQueryVariables = Exact<{
  address: Scalars['String'];
}>;


export type CollectionStatsQuery = { __typename?: 'Query', collection: { __typename?: 'Collection', address: string, itemNumber?: string | null, ownerNumber: string } };

export type ItemAttributesQueryVariables = Exact<{
  address: Scalars['String'];
}>;


export type ItemAttributesQuery = { __typename?: 'Query', item: { __typename?: 'Item', address: string, attributes: Array<{ __typename?: 'ItemAttribute', trait: string, value: string }> } };

export type ItemBreadcrumbQueryVariables = Exact<{
  address: Scalars['String'];
}>;


export type ItemBreadcrumbQuery = { __typename?: 'Query', item: { __typename?: 'Item', address: string, index?: string | null, name?: string | null, collection?: { __typename?: 'Collection', address: string, name?: string | null } | null } };

export type ItemCardQueryVariables = Exact<{
  address: Scalars['String'];
}>;


export type ItemCardQuery = { __typename?: 'Query', item: { __typename?: 'Item', address: string, index?: string | null, image?: string | null, name?: string | null, sale?: { __typename?: 'Sale', address: string, fullPrice?: string | null } | null } };

export type ItemCollectionQueryVariables = Exact<{
  address: Scalars['String'];
}>;


export type ItemCollectionQuery = { __typename?: 'Query', item: { __typename?: 'Item', address: string, collection?: { __typename?: 'Collection', address: string, name?: string | null, image?: string | null } | null } };

export type ItemContentQueryVariables = Exact<{
  address: Scalars['String'];
}>;


export type ItemContentQuery = { __typename?: 'Query', item: { __typename?: 'Item', address: string, image?: string | null } };

export type ItemDescriptionQueryVariables = Exact<{
  address: Scalars['String'];
}>;


export type ItemDescriptionQuery = { __typename?: 'Query', item: { __typename?: 'Item', address: string, index?: string | null, name?: string | null, description?: string | null, sale?: { __typename?: 'Sale', address: string } | null } };

export type ItemDetailsQueryVariables = Exact<{
  address: Scalars['String'];
}>;


export type ItemDetailsQuery = { __typename?: 'Query', item: { __typename?: 'Item', address: string, index?: string | null, royalty?: { __typename?: 'Royalty', destination: string, value: number } | null, sale?: { __typename?: 'Sale', address: string } | null } };

export type ItemOwnerQueryVariables = Exact<{
  address: Scalars['String'];
}>;


export type ItemOwnerQuery = { __typename?: 'Query', item: { __typename?: 'Item', address: string, owner?: { __typename?: 'Profile', address: string } | null } };

export type TopCollectionsQueryVariables = Exact<{
  take: Scalars['Int'];
}>;


export type TopCollectionsQuery = { __typename?: 'Query', collections: Array<{ __typename?: 'Collection', address: string, name?: string | null, image?: string | null }> };


export const CollectionInfoDocument = gql`
    query collectionInfo($address: String!) {
  collection(address: $address) {
    address
    image
    name
    description
    owner {
      address
    }
  }
}
    `;

export function useCollectionInfoQuery(options: Omit<Urql.UseQueryArgs<never, CollectionInfoQueryVariables>, 'query'> = {}) {
  return Urql.useQuery<CollectionInfoQuery>({ query: CollectionInfoDocument, ...options });
};
export const CollectionItemsDocument = gql`
    query collectionItems($address: String!, $drop: Int, $take: Int) {
  collection(address: $address) {
    address
    items(drop: $drop, take: $take) {
      address
    }
  }
}
    `;

export function useCollectionItemsQuery(options: Omit<Urql.UseQueryArgs<never, CollectionItemsQueryVariables>, 'query'> = {}) {
  return Urql.useQuery<CollectionItemsQuery>({ query: CollectionItemsDocument, ...options });
};
export const CollectionStatsDocument = gql`
    query collectionStats($address: String!) {
  collection(address: $address) {
    address
    itemNumber
    ownerNumber
  }
}
    `;

export function useCollectionStatsQuery(options: Omit<Urql.UseQueryArgs<never, CollectionStatsQueryVariables>, 'query'> = {}) {
  return Urql.useQuery<CollectionStatsQuery>({ query: CollectionStatsDocument, ...options });
};
export const ItemAttributesDocument = gql`
    query itemAttributes($address: String!) {
  item(address: $address) {
    address
    attributes {
      trait
      value
    }
  }
}
    `;

export function useItemAttributesQuery(options: Omit<Urql.UseQueryArgs<never, ItemAttributesQueryVariables>, 'query'> = {}) {
  return Urql.useQuery<ItemAttributesQuery>({ query: ItemAttributesDocument, ...options });
};
export const ItemBreadcrumbDocument = gql`
    query itemBreadcrumb($address: String!) {
  item(address: $address) {
    address
    index
    name
    collection {
      address
      name
    }
  }
}
    `;

export function useItemBreadcrumbQuery(options: Omit<Urql.UseQueryArgs<never, ItemBreadcrumbQueryVariables>, 'query'> = {}) {
  return Urql.useQuery<ItemBreadcrumbQuery>({ query: ItemBreadcrumbDocument, ...options });
};
export const ItemCardDocument = gql`
    query itemCard($address: String!) {
  item(address: $address) {
    address
    index
    image
    name
    sale {
      address
      fullPrice
    }
  }
}
    `;

export function useItemCardQuery(options: Omit<Urql.UseQueryArgs<never, ItemCardQueryVariables>, 'query'> = {}) {
  return Urql.useQuery<ItemCardQuery>({ query: ItemCardDocument, ...options });
};
export const ItemCollectionDocument = gql`
    query itemCollection($address: String!) {
  item(address: $address) {
    address
    collection {
      address
      name
      image
    }
  }
}
    `;

export function useItemCollectionQuery(options: Omit<Urql.UseQueryArgs<never, ItemCollectionQueryVariables>, 'query'> = {}) {
  return Urql.useQuery<ItemCollectionQuery>({ query: ItemCollectionDocument, ...options });
};
export const ItemContentDocument = gql`
    query itemContent($address: String!) {
  item(address: $address) {
    address
    image
  }
}
    `;

export function useItemContentQuery(options: Omit<Urql.UseQueryArgs<never, ItemContentQueryVariables>, 'query'> = {}) {
  return Urql.useQuery<ItemContentQuery>({ query: ItemContentDocument, ...options });
};
export const ItemDescriptionDocument = gql`
    query itemDescription($address: String!) {
  item(address: $address) {
    address
    index
    name
    description
    sale {
      address
    }
  }
}
    `;

export function useItemDescriptionQuery(options: Omit<Urql.UseQueryArgs<never, ItemDescriptionQueryVariables>, 'query'> = {}) {
  return Urql.useQuery<ItemDescriptionQuery>({ query: ItemDescriptionDocument, ...options });
};
export const ItemDetailsDocument = gql`
    query itemDetails($address: String!) {
  item(address: $address) {
    address
    index
    royalty {
      destination
      value
    }
    sale {
      address
    }
  }
}
    `;

export function useItemDetailsQuery(options: Omit<Urql.UseQueryArgs<never, ItemDetailsQueryVariables>, 'query'> = {}) {
  return Urql.useQuery<ItemDetailsQuery>({ query: ItemDetailsDocument, ...options });
};
export const ItemOwnerDocument = gql`
    query itemOwner($address: String!) {
  item(address: $address) {
    address
    owner {
      address
    }
  }
}
    `;

export function useItemOwnerQuery(options: Omit<Urql.UseQueryArgs<never, ItemOwnerQueryVariables>, 'query'> = {}) {
  return Urql.useQuery<ItemOwnerQuery>({ query: ItemOwnerDocument, ...options });
};
export const TopCollectionsDocument = gql`
    query topCollections($take: Int!) {
  collections(take: $take) {
    address
    name
    image
  }
}
    `;

export function useTopCollectionsQuery(options: Omit<Urql.UseQueryArgs<never, TopCollectionsQueryVariables>, 'query'> = {}) {
  return Urql.useQuery<TopCollectionsQuery>({ query: TopCollectionsDocument, ...options });
};